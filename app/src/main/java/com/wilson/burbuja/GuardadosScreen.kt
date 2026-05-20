package com.wilson.burbuja

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardadosScreen(
    listaHistorias: List<StoryData>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDeleteStory: (StoryData) -> Unit,
    onStoryClick: (StoryData) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val pullRefreshState = rememberPullToRefreshState()
    var cuentoABorrar by remember { mutableStateOf<StoryData?>(null) }

    val searchWidth by animateDpAsState(
        targetValue = if (isSearchExpanded) 300.dp else 48.dp,
        animationSpec = tween(durationMillis = 400),
        label = "searchWidth"
    )

    val historiasFiltradas = remember(searchText, listaHistorias) {
        listaHistorias.filter { historia ->
            historia.title.contains(searchText, ignoreCase = true) ||
                    historia.resultStory.contains(searchText, ignoreCase = true)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            BurbujaRefreshIndicator(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.statusBarsPadding().height(20.dp))

                // --- TOP ACTION BAR FLOTANTE (SIN SALTOS) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    // Título: animación de opacidad simple por hardware (100% a prueba de errores)
                    val titleAlpha by animateFloatAsState(
                        targetValue = if (isSearchExpanded) 0f else 1f,
                        animationSpec = tween(if (isSearchExpanded) 150 else 300),
                        label = "titleAlpha"
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .graphicsLayer(alpha = titleAlpha)
                    ) {
                        Text(
                            text = "Mis burbujas...",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Viven acá...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }

                    // Buscador expansible
                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(searchWidth)
                            .height(48.dp)
                            .clickable(enabled = !isSearchExpanded) {
                                isSearchExpanded = true
                            },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                        border = BorderStroke(
                            1.dp,
                            if (isSearchExpanded) primaryColor.copy(alpha = 0.6f) else textColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)
                        ) {
                            // Aplicamos el peso directo a la animación sin cajas extra que rompan el scope
                            AnimatedVisibility(
                                visible = isSearchExpanded,
                                enter = fadeIn(tween(300, delayMillis = 100)),
                                exit = fadeOut(tween(150)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                        if (searchText.isEmpty()) {
                                            Text(
                                                text = "Buscar...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontSize = 14.sp,
                                                color = textColor.copy(alpha = 0.5f)
                                            )
                                        }
                                        BasicTextField(
                                            value = searchText,
                                            onValueChange = { searchText = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(focusRequester),
                                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor, fontSize = 14.sp),
                                            cursorBrush = SolidColor(primaryColor),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                                        )
                                    }

                                    if (searchText.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchText = "" },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null,
                                                tint = textColor.copy(alpha = 0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            LaunchedEffect(isSearchExpanded) {
                                if (isSearchExpanded) focusRequester.requestFocus()
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(primaryColor.copy(alpha = 0.8f), CircleShape)
                                    .clickable {
                                        if (isSearchExpanded && searchText.isEmpty()) {
                                            isSearchExpanded = false
                                            focusManager.clearFocus()
                                        } else {
                                            isSearchExpanded = true
                                        }
                                    },
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- CONTENIDO DE TARJETAS ---
                if (listaHistorias.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        EmptyLibraryViewFigma(onNavigateToCreate, textColor, primaryColor)
                    }
                } else {
                    if (historiasFiltradas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                            NoResultsView(searchText, textColor)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 130.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = historiasFiltradas,
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
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(24.dp))
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
                                        onClick = { onStoryClick(historia) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (cuentoABorrar != null) {
        AlertDialog(
            onDismissRequest = { cuentoABorrar = null },
            title = { Text("Eliminar Burbuja", fontWeight = FontWeight.Bold, color = textColor) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurbujaRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val cyanColor = Color(0xFF00E5FF)
    val purpleColor = Color(0xFFB388FF)

    val infiniteTransition = rememberInfiniteTransition(label = "spinnerTransition")

    val animatedColor by infiniteTransition.animateColor(
        initialValue = primaryColor,
        targetValue = primaryColor,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                primaryColor at 0
                cyanColor at 666
                purpleColor at 1333
                primaryColor at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "colorAnim"
    )

    val pullFraction = state.distanceFraction.coerceIn(0f, 1f)

    if (pullFraction > 0f || isRefreshing) {
        Box(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 8.dp)
                .size(44.dp)
                .graphicsLayer {
                    shadowElevation = 12f
                    shape = CircleShape
                    val scaleMultiplier = if (isRefreshing) 1f else (0.5f + (pullFraction * 0.6f)).coerceAtMost(1.1f)
                    scaleX = scaleMultiplier
                    scaleY = scaleMultiplier
                }
                .background(MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = animatedColor,
                    strokeWidth = 3.dp,
                    strokeCap = StrokeCap.Round
                )
            } else {
                CircularProgressIndicator(
                    progress = { pullFraction },
                    modifier = Modifier.size(24.dp),
                    color = primaryColor,
                    strokeWidth = 3.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun StoryCard(
    story: StoryData,
    textColor: Color,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardBounce"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = tween(durationMillis = 100),
        label = "cardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(0.5.dp, textColor.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    maxLines = 2,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = story.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            }

            AsyncImage(
                model = story.photoUri,
                contentDescription = null,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun NoResultsView(query: String, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sin coincidencia...",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ninguna burbuja flotando coincide con\n\"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            color = textColor.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun EmptyLibraryViewFigma(onAction: () -> Unit, textColor: Color, primaryColor: Color) {
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