package com.wilson.burbuja

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    // 1. FRASES CON ALTO CONTRASTE TIPOGRÁFICO
    val frases = listOf(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Transformá tu ") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("entorno") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append(" en\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("relatos visuales.") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Descubrí ") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("historias ocultas\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("en cada ") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("imagen.") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Dale ") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("voz") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append(" a lo que te ") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("rodea.") }
        }
    )

    var indiceActual by remember { mutableIntStateOf(0) }
    var textoVisible by remember { mutableStateOf(false) }
    var mostrarBoton by remember { mutableStateOf(false) }

    // 2. MOTOR CINEMATOGRÁFICO EN LOOP (5 seg por frase)
    LaunchedEffect(Unit) {
        delay(800)
        var ciclosCompletados = 0

        while (true) {
            textoVisible = true
            delay(5000) // Duración de lectura

            textoVisible = false
            delay(1500) // Transición de salida + pausa en negro

            indiceActual = (indiceActual + 1) % frases.size

            // Mostramos el botón después de la primera rotación completa
            if (indiceActual == 0) ciclosCompletados++
            if (ciclosCompletados >= 1) mostrarBoton = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2A37)),
        contentAlignment = Alignment.Center
    ) {
        // --- BLOQUE DE TEXTO CENTRAL ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = textoVisible,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Text(
                    text = frases[indiceActual],
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 50.dp)
                )
            }
        }

// --- BOTÓN "EMPEZAR" REDONDEADO (Identidad Burbuja) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, start = 45.dp, end = 45.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = mostrarBoton,
                enter = fadeIn(animationSpec = tween(1500)) + slideInVertically { it / 2 }
            ) {
                // Usamos OutlinedButton que es el correcto para este estilo
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(58.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White, // Color del ripple y contenido
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color(0xFF7BCBFF).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Empezar",
                        color = Color.White, // Color explícito para forzar que se vea
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}