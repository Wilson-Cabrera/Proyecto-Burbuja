package com.wilson.burbuja

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun ResultScreen(
    storyData: StoryData,
    nombreUsuario: String,
    isAudioLoading: Boolean,
    isPlaying: Boolean,
    audioAmplitude: Float,
    onPlayAudioClick: () -> Unit,
    onStopAudioClick: () -> Unit,
    onBackClick: () -> Unit,
    onGenerateAnother: () -> Unit,
    onLogout: () -> Unit
) {
    val letraUsuario = nombreUsuario.firstOrNull()?.toString()?.uppercase() ?: "W"
    var isProfileMenuVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // DINÁMICO: Variables del tema
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val cuentoCompleto = storyData.resultStory.ifEmpty { "Generando relato..." }
    var textoMostrado by remember { mutableStateOf("") }

    LaunchedEffect(cuentoCompleto) {
        textoMostrado = ""
        cuentoCompleto.forEachIndexed { index, _ ->
            textoMostrado = cuentoCompleto.substring(0, index + 1)
            delay(15)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {

        // --- 1. FONDO E IMAGEN ---
        Box(modifier = Modifier.fillMaxSize().blur(if (isProfileMenuVisible) 20.dp else 0.dp)) {

            AsyncImage(
                model = storyData.photoUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(600.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.25f // Suavizamos la imagen de fondo
            )

            // DINÁMICO: El gradiente ahora usa el fondo dinámico (Navy o Blanco Técnico)
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(
                        colors = listOf(bgColor, bgColor.copy(alpha = 0.6f), bgColor),
                        startY = 0f, endY = 1800f
                    )
                )
            )

            // --- 2. CONTENIDO SCROLLABLE ---
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(350.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                    listOf(storyData.genero, storyData.tono, storyData.epoca).forEach { tag ->
                        Surface(
                            // DINÁMICO: Etiquetas con el color primario
                            color = primaryColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, primaryColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = tag.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Text(
                    text = storyData.title.ifEmpty { "Fragmentos de Realidad" },
                    // DINÁMICO: Título
                    color = textColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )

                Text(
                    text = textoMostrado,
                    // DINÁMICO: Cuerpo del cuento
                    color = textColor.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(180.dp))
            }
        }

        // --- 3. DOCK INFERIOR ---
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp).align(Alignment.BottomCenter)
                // DINÁMICO: Gradiente inferior para que los botones floten sobre el texto
                .background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, bgColor.copy(alpha = 0.95f), bgColor)))
                .padding(horizontal = 24.dp).padding(bottom = 30.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { /* Guardar */ },
                    modifier = Modifier.weight(1f).height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    // DINÁMICO: Botón primario
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = onPrimaryColor)
                ) {
                    Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier.weight(1.2f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    // DINÁMICO: Botón secundario outline
                    colors = ButtonDefaults.buttonColors(containerColor = textColor.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = primaryColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Regenerar", color = primaryColor, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(64.dp))
            }
        }

        // Círculo de Perfil
        Surface(
            modifier = Modifier.padding(bottom = 30.dp, end = 24.dp).align(Alignment.BottomEnd).size(56.dp).clickable { isProfileMenuVisible = !isProfileMenuVisible },
            shape = CircleShape,
            // DINÁMICO: Usamos el secundario (cyan/violeta alterno) para diferenciarlo
            color = MaterialTheme.colorScheme.secondary,
            border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = letraUsuario, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        // --- 4. REPRODUCTOR: CÚPULA PULIDA ---
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
            UniverseDomeVisualizer(
                isPlaying = isPlaying,
                isLoading = isAudioLoading,
                amplitude = audioAmplitude,
                onBackClick = onBackClick,
                onClick = { if (!isAudioLoading) { if (isPlaying) onStopAudioClick() else onPlayAudioClick() } }
            )
        }

        // Fondos del menú de perfil
        AnimatedVisibility(visible = isProfileMenuVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { isProfileMenuVisible = false })
        }
        AnimatedVisibility(
            visible = isProfileMenuVisible,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.padding(bottom = 110.dp, end = 24.dp).align(Alignment.BottomEnd)
        ) {
            // ACÁ USAMOS EL COMPONENTE QUE VIVE EN MAINACTIVITY
            ProfileMenuCard(nombreUsuario = nombreUsuario, onClose = { isProfileMenuVisible = false }, onLogout = onLogout)
        }
    }
}

// --- EL REPRODUCTOR "CÚPULA DE SONIDO" ---
@Composable
fun UniverseDomeVisualizer(
    isPlaying: Boolean,
    isLoading: Boolean,
    amplitude: Float,
    onBackClick: () -> Unit,
    onClick: () -> Unit
) {
    // DINÁMICO: Extraemos los colores AFUERA del Canvas
    val colorPrimario = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val smoothAmplitude by animateFloatAsState(
        targetValue = if (isPlaying) amplitude else 0f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "universe_time")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.verticalGradient(
                    // DINÁMICO: Difuminado superior
                    colors = listOf(bgColor, bgColor.copy(alpha = 0.8f), Color.Transparent)
                )
            )
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val domeCenter = Offset(w / 2f, 0f)
            val baseRadius = w * 0.45f
            val expandedRadius = baseRadius + (w * 0.4f * smoothAmplitude)
            val domeAlpha = 0.05f + (0.18f * smoothAmplitude)

            // 1. LA CÚPULA DE LUZ
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorPrimario.copy(alpha = domeAlpha), Color.Transparent),
                    center = domeCenter,
                    radius = expandedRadius
                ),
                radius = expandedRadius,
                center = domeCenter
            )

            // 2. LA MATRIZ DE PUNTOS
            val spacing = 65f
            val startX = (w % spacing) / 2f
            val startY = (h % spacing) / 2f

            var currentX = startX
            while (currentX <= w) {
                var currentY = startY
                while (currentY <= h) {

                    val wave = sin(time * 1.5f + (currentX * 0.005f) + (currentY * 0.005f))
                    val pulse = (wave + 1f) / 2f

                    val yProgress = 1f - (currentY / h)
                    val fadeMultiplier = (yProgress * 1.5f).coerceIn(0f, 1f).pow(1.5f)

                    val baseAlpha = 0.08f
                    val activeAlpha = (0.4f * smoothAmplitude * pulse)
                    val finalAlpha = (baseAlpha + activeAlpha) * fadeMultiplier

                    if (finalAlpha > 0.01f) {
                        drawCircle(
                            // DINÁMICO: Puntos leen el color de texto para verse tanto en Dark como en Light
                            color = textColor.copy(alpha = finalAlpha),
                            radius = 2f,
                            center = Offset(currentX, currentY)
                        )
                    }

                    currentY += spacing
                }
                currentX += spacing
            }
        }

        // --- UI DEL REPRODUCTOR ---

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .size(44.dp)
                // DINÁMICO: Fondo del botón de atrás
                .background(surfaceColor.copy(alpha = 0.6f), CircleShape)
                .border(1.dp, textColor.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = onSurfaceColor)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .size(52.dp)
                // DINÁMICO: Botón Play central
                .shadow(if (isPlaying) 12.dp else 0.dp, CircleShape, ambientColor = colorPrimario, spotColor = colorPrimario)
                .background(surfaceColor, CircleShape)
                .border(1.dp, colorPrimario.copy(alpha = 0.6f), CircleShape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = colorPrimario, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else if (!isPlaying) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = colorPrimario, modifier = Modifier.size(26.dp))
            } else {
                Icon(Icons.Default.Pause, contentDescription = "Pause", tint = colorPrimario.copy(alpha = 0.8f), modifier = Modifier.size(26.dp))
            }
        }
    }
}