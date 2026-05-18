package com.wilson.burbuja

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryData(
    val id: String = "",
    val photoUri: String = "",
    val genero: String = "",
    val narrador: String = "",
    val tono: String = "",
    val epoca: String = "",       // Reemplaza ambiente/contexto
    val detonante: String = "",   // El input: "El Detonante"
    val resultStory: String = "", // Aquí se guarda el cuento final
    val title: String = "",        // <--- NUEVO: El bolsillo para el título
    val fecha: String = ""
) : Parcelable