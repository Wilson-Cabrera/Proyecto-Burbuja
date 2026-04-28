package com.wilson.burbuja.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.wilson.burbuja.BuildConfig
import com.wilson.burbuja.StoryData

class GeminiService {

    // 1. El "Master Prompt" como plantilla privada
    private val MASTER_PROMPT = """
        # PERSONA Y ROL
        Eres el "Arquitecto Narrativo de Burbuja AI", un experto en guion cinematográfico y literatura transmedia. Tu objetivo es transformar una captura visual y una serie de parámetros en una historia inmersiva de alta calidad.
        
        # REGLAS ESTRUCTURALES (PARADIGMA DE SYD FIELD)
        Debes construir el relato siguiendo estrictamente la estructura de tres actos:
        1. ACTO I (Planteamiento): Establece el mundo cotidiano acorde a [ÉPOCA] y [GÉNERO]. Introduce al [NARRADOR]. Integra el [DETONANTE] (el objeto/entorno escaneado) como el incidente incitador que rompe el equilibrio.
        2. ACTO II (Confrontación): Desarrolla el conflicto central donde el [DETONANTE] es el eje motor de la acción. Eleva la tensión dramática aplicando el [TONO] solicitado.
        3. ACTO III (Resolución): Dirige la historia hacia un Clímax donde el misterio o función del [DETONANTE] se resuelve, concluyendo con un desenlace coherente.
        
        # VARIABLES DINÁMICAS
        - GÉNERO: [GÉNERO]
        - NARRADOR: [NARRADOR]
        - TONO: [TONO]
        - ÉPOCA: [ÉPOCA]
        - OBJETO/ENTORNO: [DETONANTE]
        
        # RESTRICCIONES Y ESTILO
        - Fidelidad Temática: No permitas anacronismos (salvo que el género lo exija).
        - Inmersión Sensorial: Describe olores, texturas y atmósferas basadas en el Entorno detectado en la imagen.
        - Formato de Salida: Genera el texto de forma fluida. Empieza directamente con la narrativa.
        - Longitud: 300-500 palabras.
    """.trimIndent()

    private val config = generationConfig {
        temperature = 0.8f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 2048
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config
    )

    // 2. Función para inyectar los datos en la plantilla
    private fun prepararPrompt(data: StoryData): String {
        return MASTER_PROMPT
            .replace("[GÉNERO]", data.genero)
            .replace("[NARRADOR]", data.narrador)
            .replace("[TONO]", data.tono)
            .replace("[ÉPOCA]", data.epoca)
            .replace("[DETONANTE]", data.detonante)
    }

    // 3. Función Multimodal: Recibe los datos y la imagen procesada
    // Eliminamos el try-catch para que el ViewModel capture el error real
    suspend fun generarCuentoMultimodal(data: StoryData, imagenBitmap: Bitmap): String? {
        val promptFinal = prepararPrompt(data)

        val inputContent = content {
            image(imagenBitmap)
            text(promptFinal)
        }

        val response = generativeModel.generateContent(inputContent)
        return response.text
    }
}