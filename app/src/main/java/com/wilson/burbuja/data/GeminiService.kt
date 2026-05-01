package com.wilson.burbuja.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.wilson.burbuja.BuildConfig
import com.wilson.burbuja.StoryData

class GeminiService {

    // 1. El "Master Prompt" como plantilla privada actualizado
    private val MASTER_PROMPT = """
        # PERSONA Y ROL
        Eres el "Arquitecto Narrativo de Burbuja AI". Tu objetivo es crear relatos inmersivos a partir de las fotografías que se capturan.
        
        # INSTRUCCIONES CRÍTICAS DE FORMATO
        1. NO menciones explícitamente "Acto I", "Planteamiento" o etiquetas similares. La estructura debe ser fluida y natural.
        2. Debes proponer un TÍTULO creativo para la historia.
        3. Devuelve tu respuesta EXACTAMENTE con este formato, usando el símbolo "||" como separador:
           TITULO: [Escribe aquí el título creativo]
           ||
           HISTORIA: [Escribe aquí el relato completo de 300-500 palabras]

        # REGLAS ESTRUCTURALES Y VISUALES
        - ANÁLISIS DE IMAGEN Y PROTAGONISMO: Analiza detalladamente la fotografía adjunta. El objeto escaneado o el paisaje capturado DEBE SER EL PROTAGONISTA absoluto de la historia (por ejemplo, si se escanea una llave, la llave es el personaje principal; si es un paisaje, el paisaje cobra vida y protagonismo).
        - Inicio: Presenta a este protagonista visual en su mundo, adaptado a la [ÉPOCA]. Usa el [DETONANTE] como el evento que pone en marcha la trama.
        - Nudo: Desarrolla el conflicto integrando elementos del entorno visible en la foto, aplicando el [TONO] y el [GÉNERO].
        - Desenlace: Resolución coherente que cierre el arco del protagonista que se capturó en la imagen.

        # VARIABLES
        - GÉNERO: [GÉNERO] | NARRADOR: [NARRADOR] | TONO: [TONO] | ÉPOCA: [ÉPOCA] | DETONANTE: [DETONANTE]
    """.trimIndent()

    private val config = generationConfig {
        temperature = 0.8f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 4096
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

    // 3. Función Multimodal: Ahora devuelve un Pair (Título, Historia)
    suspend fun generarCuentoMultimodal(data: StoryData, imagenBitmap: Bitmap): Pair<String, String>? {
        val promptFinal = prepararPrompt(data)

        val inputContent = content {
            image(imagenBitmap)
            text(promptFinal)
        }

        val response = generativeModel.generateContent(inputContent)
        val respuestaCompleta = response.text ?: return null

        // LÓGICA DE PULIDO:
        // Si la IA nos mandó el separador "||", lo dividimos en Título y Cuento.
        return if (respuestaCompleta.contains("||")) {
            val partes = respuestaCompleta.split("||")
            val tituloLimpio = partes[0].replace("TITULO:", "").trim()
            val historiaLimpia = partes[1].replace("HISTORIA:", "").trim()

            Pair(tituloLimpio, historiaLimpia)
        } else {
            // Backup por si la IA se olvida del formato ||
            Pair("Una historia de Burbuja", respuestaCompleta.trim())
        }
    }
}