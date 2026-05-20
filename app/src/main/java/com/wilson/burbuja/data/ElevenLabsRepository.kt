package com.wilson.burbuja.data

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElevenLabsRepository(private val context: Context) {

    // Cliente con paciencia (60 segundos)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: ElevenLabsApi by lazy {
        Retrofit.Builder()
            .baseUrl(ElevenLabsConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ElevenLabsApi::class.java)
    }

    suspend fun obtenerRelatoAudio(texto: String, voiceId: String, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                println("Burbuja Debug: Iniciando descarga de audio...")
                val request = TTSRequest(text = texto)
                val response = api.generarAudio(voiceId = voiceId, request = request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        println("Burbuja Debug: Descarga exitosa. Guardando archivo como $fileName...")
                        // Retornamos el archivo guardado
                        return@withContext saveToFile(body.byteStream(), fileName)
                    } else {
                        println("Burbuja Debug: La respuesta llegó vacía")
                        return@withContext null
                    }
                } else {
                    // ¡ACÁ ESTÁ LA MEJORA! Analizamos el código de error.
                    val code = response.code()
                    val errorMsg = response.errorBody()?.string()
                    println("Burbuja Debug: Error de ElevenLabs: $code - $errorMsg")

                    if (code == 401 || code == 429) {
                        // Lanzamos este error específico para que la app sepa que es un tema de cuota
                        throw Exception("SIN_CREDITOS")
                    } else {
                        throw Exception("Error de API: $code")
                    }
                }
            } catch (e: Exception) {
                // Relanzamos la excepción para que el ViewModel la atrape y maneje
                throw e
            }
        }
    }

    private fun saveToFile(inputStream: InputStream, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}