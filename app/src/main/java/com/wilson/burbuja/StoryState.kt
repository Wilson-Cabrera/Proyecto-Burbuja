package com.wilson.burbuja

sealed interface StoryState {
    object Idle : StoryState              // Estado inicial (esperando)
    object Loading : StoryState           // Mostrando un spinner/carga

    // <--- ÚNICO CAMBIO: Agregamos val title: String
    data class Success(val story: String, val title: String) : StoryState

    data class Error(val message: String) : StoryState // Algo salió mal
}