package com.wilson.burbuja

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.wilson.burbuja.data.GeminiService
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {

    // 1. Traemos nuestro servicio de IA
    private val geminiService = GeminiService()

    // 2. El estado que observará la UI (el semáforo)
    var uiState: StoryState by mutableStateOf(StoryState.Idle)
        private set

    // 3. Función principal que llamaremos desde la pantalla
    fun generarHistoria(context: Context, data: StoryData) {
        viewModelScope.launch {
            // CAMBIO DE ESTADO: Aquí se activa tu animación de carga
            uiState = StoryState.Loading

            try {
                // PROCESAMOS LA IMAGEN: De URI a Bitmap (usando Coil)
                val bitmap = obtenerBitmap(context, data.photoUri)

                if (bitmap != null) {
                    // LLAMADA A GEMINI: Le pasamos los datos y la foto
                    val cuentoGenerado = geminiService.generarCuentoMultimodal(data, bitmap)

                    // ÉXITO: Guardamos el cuento y el semáforo pasa a Verde (Success)
                    if (cuentoGenerado != null) {
                        uiState = StoryState.Success(cuentoGenerado)
                    } else {
                        uiState = StoryState.Error("Gemini no pudo crear el cuento.")
                    }
                } else {
                    uiState = StoryState.Error("No pudimos leer la imagen de la burbuja.")
                }
            } catch (e: Exception) {
                uiState = StoryState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    // 4. Función auxiliar para convertir la URI en una imagen real (Bitmap)
    private suspend fun obtenerBitmap(context: Context, uri: String): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false) // IMPORTANTE: Gemini no lee bitmaps de hardware
            .build()

        val result = loader.execute(request)
        return if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }
}