package com.wilson.burbuja

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Le da el superpoder de viajar entre pantallas
data class StoryData(
    val photoUri: String = "",
    val genero: String = "",
    val narrador: String = "",
    val tono: String = "",
    val ambiente: String = "",
    val extra: String = "",
    val resultStory: String = ""
) : Parcelable // Se conecta con el sistema de Android