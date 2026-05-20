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
        Eres el "Arquitecto Narrativo de Burbuja AI". Tu misión es transformar una captura visual (la SEMILLA) en una experiencia literaria de alta fidelidad. 
        
        # LA SEMILLA VISUAL (ANCLAJE ABSOLUTO)
        - El relato DEBE nacer de un detalle físico de la FOTO: una grieta, un reflejo, el polvo, la luz o la posición del objeto.
        - Si la foto desapareciera, el cuento dejaría de tener sentido. La imagen es el origen genético de la historia.

        # ADN NARRATIVO (EJEMPLOS DE CALIDAD)
        Imita la cadencia y profundidad de estos estilos según las variables del usuario:

        - ESTILO A (Minimalista/Objetual): "La llave no era metal; era el peso de las puertas que ya no existen. El frío del acero contra mi palma recordaba el giro exacto de una cerradura que se perdió en 1994."
        - ESTILO B (Atmosférico/Noir): "La luz de la oficina cortaba la habitación en láminas de polvo. Sobre el escritorio, la mancha de café era una confesión silenciosa que nadie se atrevía a leer."
        - ESTILO C (Épico/Fantástico): "El vidrio trizado no se rompió por un golpe, sino por el grito de algo que habitaba dentro. Cada fragmento reflejaba un cielo que no pertenecía a este mundo."

        # ESTRUCTURA Y LÍMITES (SYD FIELD INVISIBLE)
        - EXTENSIÓN: Máximo 250 palabras.
        - FORMATO: 3 párrafos (Planteamiento, Confrontación, Resolución).
        - REGLA DE ORO: "Show, Don't Tell". Prohibido el uso de palabras abstractas o "raras" (clichés como 'vasto tapiz', 'enigmático', 'susurros'). Describe texturas, olores y temperaturas.

        # INSTRUCCIONES DE SALIDA
        Devuelve tu respuesta EXACTAMENTE con este formato, usando "||" como separador:
        TITULO: [Título potente, máximo 5 palabras]
        ||
        HISTORIA: [Relato inspirado 100% en la semilla visual y los ejemplos de ADN]

        # VARIABLES DEL COAUTOR
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