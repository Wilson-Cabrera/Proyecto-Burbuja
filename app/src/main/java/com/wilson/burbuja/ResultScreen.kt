package com.wilson.burbuja

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    storyData: StoryData,
    onBackClick: () -> Unit,
    onGenerateAnother: () -> Unit
) {
    val scrollState = rememberScrollState()

    // --- LÓGICA DE ESCRITURA (Typewriter) ---
    val cuentoCompleto = if (storyData.resultStory.isEmpty()) {
        "Había una vez un fragmento de realidad capturado por una lente mágica... " +
                "Burbuja está procesando la historia basada en el género ${storyData.genero}."
    } else {
        storyData.resultStory
    }

    var textoMostrado by remember { mutableStateOf("") }

    // Cada vez que cambie el cuento, reiniciamos la escritura
    LaunchedEffect(cuentoCompleto) {
        textoMostrado = ""
        cuentoCompleto.forEachIndexed { index, _ ->
            textoMostrado = cuentoCompleto.substring(0, index + 1)
            delay(15) // Velocidad: 15ms por letra
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Fondo un poco más oscuro para que resalte el Glow
    ) {
        // --- CAPA 1: GLOW TECNOLÓGICO (Identidad de IA) ---
        Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
            drawCircle(
                color = Color(0xFF6A5CFF).copy(alpha = 0.15f),
                radius = size.minDimension / 1.2f,
                center = Offset(size.width * 0.9f, size.height * 0.3f)
            )
        }

        // --- CAPA 2: LA FOTO ---
        AsyncImage(
            model = storyData.photoUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f),
            contentScale = ContentScale.Crop,
            alpha = 0.6f // Un poco de transparencia para que se mezcle con el fondo
        )

        // --- CAPA 3: DEGRADADO SUAVE ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0F172A).copy(alpha = 0.7f),
                            Color(0xFF0F172A)
                        ),
                        startY = 300f
                    )
                )
        )

        // --- CAPA 4: BOTÓN VOLVER ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }

        // --- CAPA 5: CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(340.dp))

            // CHIPS DE DATOS (Muestra las elecciones del usuario)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                listOf(storyData.genero, storyData.tono, storyData.ambiente).forEach { tag ->
                    Surface(
                        color = Color(0xFF7B61FF).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.5.dp, Color(0xFF7B61FF).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = tag.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF7ACAFF),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // TÍTULO
            Text(
                text = "Fragmentos de Realidad",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // EL CUENTO (Con efecto typewriter)
            Text(
                text = textoMostrado,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 17.sp,
                fontFamily = Inter,
                lineHeight = 30.sp, // Interlineado cómodo para leer
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(60.dp))

            // BOTONES
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7ACAFF).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF7ACAFF))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Nueva versión", color = Color(0xFF7ACAFF), fontFamily = Inter)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFF7B61FF).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF7B61FF).copy(alpha = 0.4f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("W", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}