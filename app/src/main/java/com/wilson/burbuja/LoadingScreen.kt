package com.wilson.burbuja

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
    // Animación de pulso para el círculo central
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    // Simulación de tiempo de carga (3 segundos para no gastar tokens aún)
    LaunchedEffect(Unit) {
        delay(3000)
        onLoadingFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO (La misma foto pero con más blur)
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(50.dp),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A).copy(alpha = 0.8f)))

        // 2. CONTENIDO CENTRAL
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo animado "Cerebro de Burbuja"
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .background(Color(0xFF7B61FF).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF7B61FF), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Textos dinámicos (podríamos hacer que cambien, por ahora uno fijo)
            Text(
                text = "Burbuja está creando tu historia...",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Analizando píxeles y emociones",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontFamily = Inter,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}