package com.wilson.burbuja

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

// --- DEFINICIÓN DE FUENTES ---
val IBMPlexSans = FontFamily(
    Font(R.font.ibmplexsans_light, FontWeight.Light),
    Font(R.font.ibmplexsans_regular, FontWeight.Normal),
    Font(R.font.ibmplexsans_medium, FontWeight.Medium),
    Font(R.font.ibmplexsans_bold, FontWeight.Bold),
    Font(R.font.ibmplexsans_thin, FontWeight.Thin)
)

val Inter = FontFamily(
    Font(R.font.inter_variable, FontWeight.Light),
    Font(R.font.inter_variable, FontWeight.Normal),
    Font(R.font.inter_variable, FontWeight.Medium),
    Font(R.font.inter_variable, FontWeight.Bold)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryConfigurationScreen(
    navController: NavController,
    photoUri: String,
    onBackClick: () -> Unit = {}
) {
    // 1. ESTADOS DE SELECCIÓN
    var generoSel by remember { mutableStateOf("Misterio") }
    var narradorSel by remember { mutableStateOf("Primera persona") }
    var tonoSel by remember { mutableStateOf("Épico") }
    var epocaSel by remember { mutableStateOf("Actual") }
    var detonanteSel by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO MULTIMEDIA
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(15.dp),
            contentScale = ContentScale.Crop
        )

        // OVERLAY DARK
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A).copy(alpha = 0.85f)))

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding() // <--- EL CAMBIO: Empuja el contenido hacia arriba cuando sale el teclado
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BOTÓN VOLVER (Original)
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TÍTULOS (Original)
                Text(
                    text = "Dale forma a tu cuento",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = IBMPlexSans,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Elegí como querés que sea",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // PREVISUALIZACIÓN
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto miniatura",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .shadow(15.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )

                // --- SECCIÓN 1: GÉNERO ---
                CategoryHeader(title = "Género")
                ContextualFlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), itemCount = 5) { index ->
                    val opciones = listOf("Aventura", "Misterio", "Fantasía", "Terror", "Ciencia ficción")
                    BurbujaChip(text = opciones[index], isSelected = generoSel == opciones[index], onClick = { generoSel = opciones[index] })
                }

                // --- SECCIÓN 2: NARRADOR ---
                CategoryHeader(title = "Narrador")
                ContextualFlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), itemCount = 3) { index ->
                    val opciones = listOf("Primera persona", "Segunda persona", "Omnisciente")
                    BurbujaChip(text = opciones[index], isSelected = narradorSel == opciones[index], onClick = { narradorSel = opciones[index] })
                }

                // --- SECCIÓN 3: TONO ---
                CategoryHeader(title = "Tono")
                ContextualFlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), itemCount = 5) { index ->
                    val opciones = listOf("Épico", "Mágico", "Humorístico", "Nostálgico", "Distópico")
                    BurbujaChip(text = opciones[index], isSelected = tonoSel == opciones[index], onClick = { tonoSel = opciones[index] })
                }

                // --- SECCIÓN 4: ÉPOCA ---
                CategoryHeader(title = "Época")
                ContextualFlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), itemCount = 5) { index ->
                    val opciones = listOf("Prehistoria", "Medieval", "Actual", "Futurista", "Universo alternativo")
                    BurbujaChip(text = opciones[index], isSelected = epocaSel == opciones[index], onClick = { epocaSel = opciones[index] })
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN 5: EL DETONANTE (INPUT) ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "El Detonante", color = Color.White, fontSize = 18.sp, fontFamily = IBMPlexSans, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp), thickness = 1.dp, color = Color(0xFF74A9D2).copy(alpha = 0.5f))
                }

                OutlinedTextField(
                    value = detonanteSel,
                    onValueChange = { detonanteSel = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    placeholder = {
                        Text(
                            text = "Ej:\"Un portal se abre...\", \"Una sombra aparece...\"",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            fontFamily = Inter
                        )
                    },
                    leadingIcon = {
                        Surface(modifier = Modifier.padding(start = 8.dp).size(28.dp), shape = CircleShape, color = Color(0xFF7B61FF)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF7B61FF),
                        focusedBorderColor = Color(0xFF74A9D2),
                        unfocusedBorderColor = Color(0xFF74A9D2).copy(alpha = 0.8f),
                        focusedContainerColor = Color(0xFF74A9D2).copy(alpha = 0.1f)
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN GENERAR
                Button(
                    onClick = {
                        val encodedUri = java.net.URLEncoder.encode(photoUri, java.nio.charset.StandardCharsets.UTF_8.toString())

                        val dataParaIA = StoryData(
                            photoUri = photoUri,
                            genero = generoSel,
                            narrador = narradorSel,
                            tono = tonoSel,
                            epoca = epocaSel,
                            detonante = detonanteSel
                        )

                        navController.currentBackStackEntry?.savedStateHandle?.set("storyData", dataParaIA)
                        navController.navigate("loading/$encodedUri")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(12.dp, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF74A9D2),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Generar", fontSize = 18.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color(0xFFffffff))
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

// --- CATEGORY HEADER Y BURBUJA CHIP (Se mantienen igual) ---
@Composable
fun CategoryHeader(title: String) {
    Spacer(modifier = Modifier.height(28.dp))
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            color = Color(0xFFffffff),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            fontFamily = Inter
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            thickness = 0.9.dp,
            color = Color(0xFF7ACAFF).copy(alpha = 0.9f)
        )
    }
}

@Composable
fun BurbujaChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF7B61FF) else Color(0xFF1F2A37).copy(0.5f),
        border = if (isSelected) null else BorderStroke(0.9.dp, Color(0xFF74A9D2).copy(0.9f)),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(0.8f),
            fontSize = 13.sp,
            fontFamily = Inter,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}