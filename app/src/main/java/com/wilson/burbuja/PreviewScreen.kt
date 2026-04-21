package com.wilson.burbuja

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset // Faltaba esta
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun PreviewScreen(navController: NavController, photoUri: String) {
    var mostrarContenido by remember { mutableStateOf(false) }

    // Decodificamos la URI por seguridad
    val decodedUri = remember(photoUri) {
        URLDecoder.decode(photoUri, StandardCharsets.UTF_8.toString())
    }

    LaunchedEffect(Unit) {
        delay(300)
        mostrarContenido = true
    }

    val fondoGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF2D3B4D), Color(0xFF111827)),
        radius = 2000f
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(fondoGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CONTENEDOR DE LA IMAGEN
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 30.dp, vertical = 60.dp)
                    .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF7ACAFF))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black)
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(24.dp))
            ) {
                if (mostrarContenido) {
                    AsyncImage(
                        model = decodedUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = "¿ES ESTE EL OBJETO?",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // BARRA DE CONTROL
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
                    .height(90.dp),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(45.dp),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BOTÓN DESCARTAR
                    IconButton(
                        onClick = {
                            try {
                                val file = File(Uri.parse(decodedUri).path ?: "")
                                if (file.exists()) file.delete()
                            } catch (e: Exception) { e.printStackTrace() }
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, "Descartar", tint = Color.White)
                    }

                    // BOTÓN CONFIRMAR
// BOTÓN CONFIRMAR
                    Button(
                        onClick = {
                            // 1. Codificamos la URI para que sea segura para la navegación
                            val encodedUri = java.net.URLEncoder.encode(photoUri, java.nio.charset.StandardCharsets.UTF_8.toString())

                            // 2. Navegamos a la ruta que definimos en el MainActivity
                            navController.navigate("story_configuration/$encodedUri")
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .width(130.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7ACAFF)),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color(0xFF111827))
                        Spacer(Modifier.width(8.dp))
                        Text("LISTO", color = Color(0xFF111827), fontWeight = FontWeight.Bold)
                    }

                }
            }
        }
    }
}

