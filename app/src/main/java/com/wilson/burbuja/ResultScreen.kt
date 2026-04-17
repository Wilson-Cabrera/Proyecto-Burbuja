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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    // --- 1. EFECTO MÁQUINA DE ESCRIBIR ---
    val cuentoCompleto = storyData.resultStory.ifEmpty { "Generando relato..." }
    var textoMostrado by remember { mutableStateOf("") }

    LaunchedEffect(cuentoCompleto) {
        textoMostrado = ""
        cuentoCompleto.forEachIndexed { index, _ ->
            textoMostrado = cuentoCompleto.substring(0, index + 1)
            delay(15)
        }
    }

    // --- 2. CONTENEDOR PRINCIPAL (BOX) ---
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {

        // CAPA 1: Glow de fondo (Identidad visual)
        Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
            drawCircle(
                color = Color(0xFF6A5CFF).copy(alpha = 0.15f),
                radius = size.minDimension / 1.2f,
                center = Offset(size.width * 0.9f, size.height * 0.3f)
            )
        }

        // CAPA 2: La Imagen capturada
        AsyncImage(
            model = storyData.photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        // CAPA 3: Degradado para legibilidad
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.7f), Color(0xFF0F172A)),
                    startY = 300f
                )
            )
        )

        // --- CAPA 4: CONTENIDO SCROLLABLE ---
        // IMPORTANTE: Esta capa está debajo del botón de volver para no tapar los clics.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(340.dp))

            // Etiquetas de la IA (Género, Tono, etc.)
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

            Text(
                text = "Fragmentos de Realidad",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = textoMostrado,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 17.sp,
                fontFamily = Inter,
                lineHeight = 30.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(60.dp))

            // --- ACCIONES INFERIORES ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Guardar (Primario)
                Button(
                    onClick = { println("💾 Guardando historia...") },
                    modifier = Modifier.weight(1.2f).height(56.dp).shadow(4.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7ACAFF),
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar", fontFamily = Inter, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Botón Otra/Regenerar (Secundario)
                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7ACAFF).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = Color(0xFF7ACAFF))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Otra",
                        color = Color(0xFF7ACAFF),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                // Círculo Perfil
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFF7B61FF).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF7B61FF).copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("W", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- CAPA 5: BOTÓN VOLVER (¡ARRIBA DE TODO!) ---
        // Al estar al final del Box, es la capa superior y recibe todos los clics.
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
    }
}