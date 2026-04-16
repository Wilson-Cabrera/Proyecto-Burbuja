package com.wilson.burbuja

/**
 * Esta es la "mochila" de datos.
 * Agrupa todo lo que necesitamos para armar el prompt de la IA.
 */
data class StoryData(
    val photoUri: String,
    val genero: String,
    val narrador: String,
    val tono: String,
    val ambiente: String,
    val extra: String,
    val resultStory: String = ""
)