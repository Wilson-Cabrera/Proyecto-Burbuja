package com.wilson.burbuja

sealed interface StoryState {
    object Idle : StoryState              // Estado inicial (esperando)
    object Loading : StoryState           // Mostrando un spinner/carga
    data class Success(val story: String) : StoryState // Cuento generado con éxito
    data class Error(val message: String) : StoryState // Algo salió mal
}