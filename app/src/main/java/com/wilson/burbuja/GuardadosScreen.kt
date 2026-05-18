package com.wilson.burbuja

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardadosScreen(
    listaHistorias: List<StoryData>,
    onDeleteStory: (StoryData) -> Unit,
    onStoryClick: (StoryData) -> Unit, // <-- NUEVO: Para escuchar el toque en la tarjeta
    onNavigateToCreate: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    var cuentoABorrar by remember { mutableStateOf<StoryData?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Buscar...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.5f)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(primaryColor.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            if (listaHistorias.isEmpty()) {
                EmptyLibraryViewFigma(onNavigateToCreate, textColor, primaryColor)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mis burbujas...",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Viven acá...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 40.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = listaHistorias,
                        key = { it.id }
                    ) { historia ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    cuentoABorrar = historia
                                    false
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val backgroundAlpha by animateFloatAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0f,
                                    label = "alphaAnim"
                                )

                                val modernRed = Color(0xFFD32F2F)

                                val difuminadoGradient = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        modernRed.copy(alpha = 0.1f),
                                        modernRed.copy(alpha = 0.7f)
                                    ),
                                    startX = 0f,
                                )

                                val scale by animateFloatAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
                                    label = "scaleAnim"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(CircleShape)
                                        .graphicsLayer(alpha = backgroundAlpha)
                                        .background(difuminadoGradient),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(end = 28.dp)
                                            .scale(scale)
                                    )
                                }
                            }
                        ) {
                            StoryCard(
                                story = historia,
                                textColor = textColor,
                                primaryColor = primaryColor,
                                onClick = { onStoryClick(historia) } // <-- NUEVO: Dispara el clic
                            )
                        }
                    }
                }
            }
        }
    }

    if (cuentoABorrar != null) {
        AlertDialog(
            onDismissRequest = { cuentoABorrar = null },
            title = {
                Text("Eliminar Burbuja", fontWeight = FontWeight.Bold, color = textColor)
            },
            text = {
                Text(
                    text = buildAnnotatedString {
                        append("¿Estás seguro de que querés borrar el relato ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)) {
                            append("'${cuentoABorrar?.title}'")
                        }
                        append("? Esta acción no se puede deshacer.")
                    },
                    color = textColor.copy(alpha = 0.8f)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteStory(cuentoABorrar!!)
                        cuentoABorrar = null
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { cuentoABorrar = null }) {
                    Text("Cancelar", color = primaryColor)
                }
            }
        )
    }
}

@Composable
fun StoryCard(
    story: StoryData,
    textColor: Color,
    primaryColor: Color,
    onClick: () -> Unit // <-- NUEVO: Recibe la función de clic
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(CircleShape) // Corta el efecto de toque a la forma de cápsula
            .clickable { onClick() }, // <-- NUEVO: Hace que la tarjeta sea apretable
        shape = CircleShape,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.5f)
                )
            }

            AsyncImage(
                model = story.photoUri,
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun EmptyLibraryViewFigma(onAction: () -> Unit, textColor: Color, primaryColor: Color) {
    // ... (Tu código de EmptyLibraryViewFigma queda exactamente igual) ...
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = buildAnnotatedString {
                append("Tu ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("universo") }
                append(" está en\nsilencio...")
            },
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            color = textColor,
            modifier = Modifier.padding(top = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .offset(y = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crea tu primera burbuja para que empiece\na flotar en tu biblioteca.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    color = textColor.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedButton(
                    onClick = onAction,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                ) {
                    Text(
                        text = "Crea tu primera burbuja...",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}