package com.wilson.burbuja

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val navyBg = Color(0xFF1F2A37)
    val celesteIA = Color(0xFF7BCBFF)
    val googleButtonBg = Color(0xFFDDE2E8) // Gris claro del diseño original

    var touchPos by remember { mutableStateOf(Offset(-500f, -500f)) }
    var isTouching by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(navyBg)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isTouching = event.changes.any { it.pressed }
                        touchPos = event.changes.first().position
                    }
                }
            }
    ) {
        // --- FONDO: CAMPO DE ENFOQUE ---
        CampoDeEnfoque(celesteIA, touchPos, isTouching, pulse)

        // --- INTERFAZ ORIGINAL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 30.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Los textos van arriba según tu captura
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "BIENVENIDO",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lo que ves puede ser una historia",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light
            )

            // Empujamos los botones hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN GOOGLE
            Button(
                onClick = { /* Lógica de Google */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = googleButtonBg)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Representación del logo "G"
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFF7B96B2).copy(alpha = 0.8f) // Color azulado del logo G en tu imagen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Iniciar sesión con Google",
                        color = Color(0xFF1F2A37),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÓN CREAR HISTORIA (Outlined Celeste)
            OutlinedButton(
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(2.dp, celesteIA),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(
                    text = "Crear mi historia",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CampoDeEnfoque(color: Color, touchPos: Offset, isTouching: Boolean, pulse: Float) {
    val smoothX by animateFloatAsState(if (isTouching) touchPos.x else 540f, spring(Spring.DampingRatioLowBouncy))
    val smoothY by animateFloatAsState(if (isTouching) touchPos.y else 700f, spring(Spring.DampingRatioLowBouncy))

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(smoothX, smoothY)
        val bubbleRadius = size.width * 0.45f * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                center = center,
                radius = bubbleRadius * 1.5f
            )
        )

        val spacing = 55f
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            for (y in 0..size.height.toInt() step spacing.toInt()) {
                val point = Offset(x.toFloat(), y.toFloat())
                val dist = (point - center).getDistance()

                var offsetPoint = point
                if (dist < bubbleRadius) {
                    val factor = (1f - dist / bubbleRadius).pow(2)
                    val dir = (point - center) / dist
                    offsetPoint = point + dir * (factor * 40f)
                }

                val alpha = if (dist < bubbleRadius) 0.5f else 0.1f

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = if (dist < bubbleRadius) 2f else 1f,
                    center = offsetPoint
                )
            }
        }
    }
}