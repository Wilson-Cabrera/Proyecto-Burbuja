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
import androidx.compose.ui.geometry.Offset
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

    // DINÁMICO: Leemos los colores principales del tema actual
    val colorFondo = MaterialTheme.colorScheme.background
    val colorSuperficie = MaterialTheme.colorScheme.surface
    val colorTexto = MaterialTheme.colorScheme.onBackground
    val colorPrimario = MaterialTheme.colorScheme.primary

    // DINÁMICO: Un gradiente suave entre la superficie y el fondo para dar profundidad
    val fondoGradient = Brush.radialGradient(
        colors = listOf(colorSuperficie, colorFondo),
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
                    // DINÁMICO: La sombra ahora brilla con el color primario (Celeste IA o Violeta)
                    .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = colorPrimario)
                    .clip(RoundedCornerShape(24.dp))
                    // La base de la foto queda en negro para mantener el contraste visual tipo "revelado"
                    .background(Color.Black)
                    // DINÁMICO: Borde adaptado al tema
                    .border(BorderStroke(1.dp, colorTexto.copy(alpha = 0.1f)), RoundedCornerShape(24.dp))
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
                // DINÁMICO: Texto adaptado al fondo
                color = colorTexto.copy(alpha = 0.6f),
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
                // DINÁMICO: Contenedor con el color de superficie
                color = colorSuperficie.copy(alpha = 0.8f),
                shape = RoundedCornerShape(45.dp),
                border = BorderStroke(0.5.dp, colorTexto.copy(alpha = 0.1f))
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
                            // DINÁMICO: Fondo y borde dinámicos
                            .background(colorTexto.copy(alpha = 0.08f), CircleShape)
                            .border(BorderStroke(1.dp, colorTexto.copy(alpha = 0.1f)), CircleShape)
                    ) {
                        // DINÁMICO: Ícono en color que contrasta
                        Icon(Icons.Default.Close, "Descartar", tint = colorTexto)
                    }

                    // BOTÓN CONFIRMAR
                    Button(
                        onClick = {
                            val encodedUri = java.net.URLEncoder.encode(photoUri, java.nio.charset.StandardCharsets.UTF_8.toString())
                            navController.navigate("story_configuration/$encodedUri")
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .width(130.dp),
                        // DINÁMICO: Colores del botón atados al Primary y OnPrimary
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorPrimario,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("LISTO", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }

                }
            }
        }
    }
}