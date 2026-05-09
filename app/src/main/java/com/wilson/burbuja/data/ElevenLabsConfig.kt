package com.wilson.burbuja.data

import com.wilson.burbuja.BuildConfig

object ElevenLabsConfig {
    // Tomamos la clave generada de forma segura
    val API_KEY = BuildConfig.ELEVEN_LABS_KEY

    const val BASE_URL = "https://api.elevenlabs.io/v1/"

    // Voz de Ivan (Sabio y Narrativo) asignada para todos los géneros por ahora
    const val VOICE_MISTERIO = "HbJsSWyoEeSXQTa8L0EL"
    const val VOICE_FANTASIA = "HbJsSWyoEeSXQTa8L0EL"
    const val VOICE_TERROR = "HbJsSWyoEeSXQTa8L0EL"
}