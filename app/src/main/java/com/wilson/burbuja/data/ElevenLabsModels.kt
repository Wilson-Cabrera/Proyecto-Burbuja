package com.wilson.burbuja.data

// El cuerpo de la petición que le enviamos a la API
data class TTSRequest(
    val text: String,
    val model_id: String = "eleven_multilingual_v2", // El mejor modelo para español
    val voice_settings: VoiceSettings = VoiceSettings()
)

// Ajustes finos de la voz (estos valores por defecto dan un tono muy natural)
data class VoiceSettings(
    val stability: Float = 0.5f,
    val similarity_boost: Float = 0.75f
)