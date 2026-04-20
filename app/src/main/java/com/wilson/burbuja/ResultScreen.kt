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
    nombreUsuario: String, // <--- 1. RECIBIMOS EL NOMBRE DESDE MAINACTIVITY
    onBackClick: () -> Unit,
    onGenerateAnother: () -> Unit
) {
    // 2. CALCULAMOS LA INICIAL DINÁMICA
    val letraUsuario = nombreUsuario.firstOrNull()?.toString()?.uppercase() ?: "U"

    var isProfileMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val screenBackground = Color(0xFF0F172A)

    // --- LÓGICA DE ESCRITURA ---
    val cuentoCompleto = storyData.resultStory.ifEmpty { "Generando relato..." }
    var textoMostrado by remember { mutableStateOf("") }

    LaunchedEffect(cuentoCompleto) {
        textoMostrado = ""
        cuentoCompleto.forEachIndexed { index, _ ->
            textoMostrado = cuentoCompleto.substring(0, index + 1)
            delay(15)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(screenBackground)) {

        // --- CAPA 1: FONDO Y FOTO (DIFUMINABLE) ---
        Box(modifier = Modifier.fillMaxSize().blur(if (isProfileMenuVisible) 20.dp else 0.dp)) {
            // Glow de fondo
            Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
                drawCircle(
                    color = Color(0xFF6A5CFF).copy(alpha = 0.15f),
                    radius = size.minDimension / 1.2f,
                    center = Offset(size.width * 0.9f, size.height * 0.3f)
                )
            }

            // Imagen
            AsyncImage(
                model = storyData.photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )

            // Degradado superior
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, screenBackground.copy(alpha = 0.8f), screenBackground),
                        startY = 300f
                    )
                )
            )

            // --- CAPA 2: CONTENIDO SCROLLABLE ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(340.dp))

                // CHIPS DE DATOS
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
                                fontSize = 10.sp, color = Color(0xFF7ACAFF), fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(text = "Fragmentos de Realidad", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = textoMostrado,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        // --- CAPA 3: DOCK INFERIOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, screenBackground.copy(alpha = 0.95f), screenBackground)
                    )
                )
                .padding(horizontal = 28.dp).padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { /* Guardar */ },
                    modifier = Modifier.weight(1.2f).height(56.dp).shadow(4.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7ACAFF), contentColor = Color(0xFF0F172A))
                ) {
                    Icon(Icons.Default.BookmarkBorder, null); Spacer(Modifier.width(8.dp)); Text("Guardar", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7ACAFF).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.3f))
                ) {
                    Text("Otra", color = Color(0xFF7ACAFF))
                }
                Spacer(modifier = Modifier.weight(0.3f))
                Box(modifier = Modifier.size(56.dp))
            }
        }

        // --- CAPA 4: ELEMENTOS NÍTIDOS Y MENÚ ---
        AnimatedVisibility(visible = isProfileMenuVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { isProfileMenuVisible = false })
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.statusBarsPadding().padding(16.dp).align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }

        Surface(
            modifier = Modifier.padding(bottom = 40.dp, end = 28.dp).align(Alignment.BottomEnd).size(56.dp)
                .clickable { isProfileMenuVisible = !isProfileMenuVisible },
            shape = CircleShape,
            color = Color(0xFF7B61FF),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            // 3. APLICAMOS LA LETRA DINÁMICA A LA ELIPSE
            Box(contentAlignment = Alignment.Center) { Text(text = letraUsuario, color = Color.White, fontWeight = FontWeight.Bold) }
        }

        AnimatedVisibility(
            visible = isProfileMenuVisible,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.padding(bottom = 110.dp, end = 28.dp).align(Alignment.BottomEnd)
        ) {
            // 4. PASAMOS EL NOMBRE A LA TARJETA DEL MENÚ
            ProfileMenuCard(nombreUsuario = nombreUsuario, onClose = { isProfileMenuVisible = false })
        }
    }
}

// --- COMPONENTES AUXILIARES ACTUALIZADOS ---

@Composable
fun ProfileMenuCard(
    nombreUsuario: String = "Wilson",
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¡Hola $nombreUsuario!", color = Color(0xFF64748B), fontSize = 14.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem(Icons.Default.NightsStay, "Modo oscuro")
            ProfileMenuItem(Icons.Default.Share, "Compartir")
            ProfileMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión")
        }
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 10.dp)) {
        Icon(icon, null, tint = Color(0xFF1E293B), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color(0xFF1E293B), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}