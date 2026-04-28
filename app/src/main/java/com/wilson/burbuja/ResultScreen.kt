package com.wilson.burbuja

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    storyData: StoryData,
    nombreUsuario: String,
    onBackClick: () -> Unit,
    onGenerateAnother: () -> Unit,
    onLogout: () -> Unit
) {
    val letraUsuario = nombreUsuario.firstOrNull()?.toString()?.uppercase() ?: "W"
    var isProfileMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Paleta Wilson
    val navyBg = Color(0xFF1F2A37)
    val celesteIA = Color(0xFF7BCBFF)
    val violetaRegenerativo = Color(0xFF6A5CFF)

    val cuentoCompleto = storyData.resultStory.ifEmpty { "Generando relato..." }
    var textoMostrado by remember { mutableStateOf("") }

    // Animación de escritura
    LaunchedEffect(cuentoCompleto) {
        textoMostrado = ""
        cuentoCompleto.forEachIndexed { index, _ ->
            textoMostrado = cuentoCompleto.substring(0, index + 1)
            delay(15)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(navyBg)) {

        // --- 1. FONDO AMBIENTAL INTEGRADO ---
        Box(modifier = Modifier.fillMaxSize().blur(if (isProfileMenuVisible) 20.dp else 0.dp)) {

            // Glow ambiental
            Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
                drawCircle(
                    color = violetaRegenerativo.copy(alpha = 0.15f),
                    radius = size.minDimension / 1.2f,
                    center = Offset(size.width * 0.9f, size.height * 0.3f)
                )
            }

            // Imagen con altura extendida para fundido suave
            AsyncImage(
                model = storyData.photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(600.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )

            // Degradado Maestro Inferior
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            navyBg.copy(alpha = 0.4f),
                            navyBg.copy(alpha = 0.85f),
                            navyBg
                        ),
                        startY = 0f,
                        endY = 1600f
                    )
                )
            )

            // --- 2. CONTENIDO SCROLLABLE ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(380.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    listOf(storyData.genero, storyData.tono, storyData.epoca).forEach { tag ->
                        Surface(
                            color = violetaRegenerativo.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, violetaRegenerativo.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = tag.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp, color = celesteIA, fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Fragmentos de Realidad",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = textoMostrado,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Start,
                    fontFamily = Inter
                )

                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        // --- 3. TOP FADE OVERLAY (Blindado para la Status Bar) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Un poco más de altura para un fundido elegante
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to navyBg,          // Sólido absoluto en la barra de estado
                            0.3f to navyBg,          // Sigue sólido hasta pasar el nombre de la app
                            0.5f to navyBg.copy(alpha = 0.8f), // Comienza a fundirse
                            1.0f to Color.Transparent // Desaparece para integrar el scroll
                        )
                    )
                )
        )

        // --- 4. HEADER (Estático por encima del Top Fade) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart).background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }

            Text(
                text = "BURBUJA AI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // --- 5. DOCK INFERIOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, navyBg.copy(alpha = 0.95f), navyBg)
                    )
                )
                .padding(horizontal = 24.dp).padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { /* Lógica Guardar */ },
                    modifier = Modifier.weight(1f).height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = celesteIA, contentColor = navyBg)
                ) {
                    Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar", fontWeight = FontWeight.Bold, fontFamily = Inter)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier.weight(1.2f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, celesteIA.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = celesteIA, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Regenerar", color = celesteIA, fontWeight = FontWeight.SemiBold, fontFamily = Inter)
                }

                Spacer(modifier = Modifier.width(64.dp))
            }
        }

        // --- 6. PERFIL ---
        Surface(
            modifier = Modifier.padding(bottom = 40.dp, end = 24.dp).align(Alignment.BottomEnd).size(56.dp)
                .clickable { isProfileMenuVisible = !isProfileMenuVisible },
            shape = CircleShape,
            color = violetaRegenerativo,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = letraUsuario, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        AnimatedVisibility(visible = isProfileMenuVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { isProfileMenuVisible = false })
        }

        AnimatedVisibility(
            visible = isProfileMenuVisible,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.padding(bottom = 110.dp, end = 24.dp).align(Alignment.BottomEnd)
        ) {
            ProfileMenuCard(
                nombreUsuario = nombreUsuario,
                onClose = { isProfileMenuVisible = false },
                onLogout = onLogout
            )
        }
    }
}

// --- SUBCOMPONENTES ---

@Composable
fun ProfileMenuCard(nombreUsuario: String, onClose: () -> Unit, onLogout: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hola, $nombreUsuario", color = Color(0xFF64748B), fontSize = 14.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem(Icons.Default.NightsStay, "Modo oscuro") {}
            ProfileMenuItem(Icons.Default.Share, "Compartir") {}
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black.copy(alpha = 0.05f))
            ProfileMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión", onLogout)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp)
    ) {
        Icon(icon, null, tint = Color(0xFF1E293B), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Color(0xFF1E293B), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}