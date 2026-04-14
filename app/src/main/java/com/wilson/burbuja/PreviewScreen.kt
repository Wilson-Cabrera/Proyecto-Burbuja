package com.wilson.burbuja

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File

@Composable
fun PreviewScreen(navController: NavController, photoUri: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // BOTÓN REINTENTAR: Borra la foto y vuelve
            FilledIconButton(
                onClick = {
                    try {
                        // Intentamos borrar el archivo para no dejar basura en el cache
                        val uri = Uri.parse(photoUri)
                        val file = File(uri.path ?: "")
                        if (file.exists()) file.delete()
                    } catch (e: Exception) { e.printStackTrace() }

                    navController.popBackStack()
                },
                modifier = Modifier.size(70.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Descartar", tint = Color.White)
            }

            // BOTÓN CONTINUAR
            FilledIconButton(
                onClick = { /* Aquí vendrá la lógica de la IA */ },
                modifier = Modifier.size(85.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7ACAFF))
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirmar", tint = Color.Black, modifier = Modifier.size(40.dp))
            }
        }
    }
}