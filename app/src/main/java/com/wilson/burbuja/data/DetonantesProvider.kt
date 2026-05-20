package com.wilson.burbuja.data

object DetonantesProvider {
    private val semillas = listOf(
        "Un portal se abre.",
        "Una sombra aparece.",
        "El tiempo se detiene.",
        "Un susurro me nombra.",
        "El objeto se agrieta.",
        "Una luz parpadea.",
        "Un latido interno.",
        "El reflejo cambia.",
        "Algo se mueve dentro.",
        "Un frío repentino.",
        "Una marca brilla.",
        "El suelo vibra.",
        "Un aroma extraño.",
        "El metal se calienta.",
        "Un código aparece."
    )

    /**
     * Retorna un detonante al azar de la lista.
     */
    fun obtenerAleatorio(): String = semillas.random()
}