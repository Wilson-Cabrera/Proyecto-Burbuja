package com.wilson.burbuja

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    photoUri: String,
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

    LaunchedEffect(Unit) {
        delay(4000)
        onLoadingFinished()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1F2A37))) {

        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            // CORRECCIÓN: Creamos un Offset explícito para el centro
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension

            // Onda 1: Violeta (#7B61FF)
            drawCircle(
                color = Color(0xFF7B61FF).copy(alpha = 1f - waveProgress),
                radius = maxRadius * waveProgress,
                center = centerOffset, // Ahora sí es un Offset
                style = Stroke(width = 2.dp.toPx())
            )

            // Onda 2: Celeste (#7ACAFF)
            val delayedProgress = (waveProgress + 0.5f) % 1f
            drawCircle(
                color = Color(0xFF7ACAFF).copy(alpha = 1f - delayedProgress),
                radius = maxRadius * delayedProgress,
                center = centerOffset, // Ahora sí es un Offset
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BURBUJA IA",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Analizando fragmentos de realidad...",
                color = Color(0xFF7ACAFF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}