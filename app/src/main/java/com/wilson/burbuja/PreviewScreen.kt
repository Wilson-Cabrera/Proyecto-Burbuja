package com.wilson.burbuja

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun PreviewScreen(navController: NavController, photoUri: String) {
    // 1. Estado para retrasar el renderizado pesado
    var mostrarContenido by remember { mutableStateOf(false) }

    // 2. Esperamos a que la transición del NavHost termine (aprox 400-500ms)
    LaunchedEffect(Unit) {
        delay(450)
        mostrarContenido = true
    }

    // Usamos el fondo oscuro que definimos en el NavHost para que no haya saltos
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1F2A37))
    ) {
        // --- LA IMAGEN CON REVELADO SUAVE ---
        AnimatedVisibility(
            visible = mostrarContenido,
            enter = fadeIn(tween(600)) + scaleIn(initialScale = 0.98f, animationSpec = tween(600)),
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // --- CONTROLES INFERIORES ---
        // Los mostramos un poquito después que la foto para dar sensación de orden
        AnimatedVisibility(
            visible = mostrarContenido,
            enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(tween(800)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BOTÓN REINTENTAR
                Surface(
                    onClick = {
                        try {
                            val uri = Uri.parse(photoUri)
                            val file = File(uri.path ?: "")
                            if (file.exists()) file.delete()
                        } catch (e: Exception) { e.printStackTrace() }
                        navController.popBackStack()
                    },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, "Descartar", tint = Color.White)
                    }
                }

                // BOTÓN CONTINUAR (El principal de la acción)
                Surface(
                    onClick = { /* Aquí dispararemos la magia de Burbuja */ },
                    shape = CircleShape,
                    color = Color(0xFF7ACAFF),
                    modifier = Modifier.size(80.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            "Confirmar",
                            tint = Color(0xFF1F2A37),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}