package com.wilson.burbuja

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

    val frases = listOf(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Transformá tu entorno en una\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("experiencia visual reimaginada") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Dale voz a lo cotidiano y descubrí el\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("relato oculto en cada imagen") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Revelá lo invisible y conectá con la\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("narrativa que la IA construye para vos") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Convertite en el autor de historias que\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("redefinen tu forma de ver el mundo") }
        },
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraLight)) { append("Explorá tu imaginación sin límites y\n") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("empezá a escribir tu próximo relato") }
        }
    )

    var indiceActual by remember { mutableIntStateOf(0) }
    var textoVisible by remember { mutableStateOf(false) }
    var mostrarBoton by remember { mutableStateOf(false) }
    var mostrarLogo by remember { mutableStateOf(false) } // Controlamos el logo

    // --- MOTOR CINEMATOGRÁFICO AJUSTADO ---
    LaunchedEffect(Unit) {
        delay(400) // Aparece el logo un poquito antes que el primer texto
        mostrarLogo = true
        delay(400) // Pausa inicial estética para el texto

        while (true) {
            textoVisible = true

            // ACTIVACIÓN TEMPRANA DEL BOTÓN
            if (indiceActual == 0 && !mostrarBoton) {
                delay(3000)
                mostrarBoton = true
                delay(2000)
            } else {
                delay(5000)
            }

            textoVisible = false
            delay(1500)

            indiceActual = (indiceActual + 1) % frases.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2A37))
    ) {
        // --- LOGO (ISOTIPO) EN LA PARTE SUPERIOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp), // Separación de la barra de estado
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = mostrarLogo,
                enter = fadeIn(animationSpec = tween(1500))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Burbuja AI",
                    modifier = Modifier.size(200.dp), // Tamaño sutil para no robar protagonismo
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- TEXTO CENTRAL ---
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                AnimatedVisibility(
                    visible = textoVisible,
                    enter = fadeIn(animationSpec = tween(1200)),
                    exit = fadeOut(animationSpec = tween(1000))
                ) {
                    Text(
                        text = frases[indiceActual],
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- BOTÓN "EMPEZAR" ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = mostrarBoton,
                enter = fadeIn(animationSpec = tween(1500)) + slideInVertically { it / 3 }
            ) {
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(54.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color(0xFF7BCBFF).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Empezar",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}