package com.wilson.burbuja

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun LoadingScreen(
    navController: NavController,
    photoUri: String,
    viewModel: StoryViewModel,
    onLoadingFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")

    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave"
    )

    val currentState = viewModel.uiState

    // --- LÓGICA DE ESCUCHA ---
    LaunchedEffect(currentState) {
        if (currentState is StoryState.Success) {
            val handleAnterior = navController.previousBackStackEntry?.savedStateHandle
            val storyData = handleAnterior?.get<StoryData>("storyData") ?: StoryData()
            val mochilaActualizada = storyData.copy(resultStory = currentState.story)
            handleAnterior?.set("storyData", mochilaActualizada)
            onLoadingFinished()
        }
        // Eliminamos el bloque de Error de aquí para que NO navegue hacia atrás automáticamente.
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1F2A37))) {
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        // Solo mostramos la animación si NO hay error
        if (currentState !is StoryState.Error) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.minDimension

                drawCircle(
                    color = Color(0xFF7B61FF).copy(alpha = 1f - waveProgress),
                    radius = maxRadius * waveProgress,
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )

                val delayedProgress = (waveProgress + 0.5f) % 1f
                drawCircle(
                    color = Color(0xFF7ACAFF).copy(alpha = 1f - delayedProgress),
                    radius = maxRadius * delayedProgress,
                    center = centerOffset,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BURBUJA IA",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // MANEJO VISUAL DE ESTADOS
            when (currentState) {
                is StoryState.Error -> {
                    // SI HAY ERROR: Mostramos el mensaje en rojo y un botón
                    Text(
                        text = "Algo salió mal:\n${currentState.message}",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                    ) {
                        Text("Volver a intentar", color = Color.White, fontFamily = Inter)
                    }
                }
                else -> {
                    // SI ESTÁ CARGANDO: Texto normal
                    val mensajeCarga = if (currentState is StoryState.Loading) {
                        "Sintetizando variables narrativas..."
                    } else {
                        "Analizando fragmentos de realidad..."
                    }
                    Text(
                        text = mensajeCarga,
                        color = Color(0xFF7ACAFF),
                        fontSize = 14.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}