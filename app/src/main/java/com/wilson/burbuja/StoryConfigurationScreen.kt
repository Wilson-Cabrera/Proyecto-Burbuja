package com.wilson.burbuja

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalLayoutApi::class) // Necesario para ContextualFlowRow
@Composable
fun StoryConfigurationScreen(
    navController: NavController,
    photoUri: String,
    onBackClick: () -> Unit = {}
) {
    // 1. ESTADOS (Siempre arriba de todo)
    var generoSel by remember { mutableStateOf("Misterio") }
    var narradorSel by remember { mutableStateOf("Primera persona") }
    var tonoSel by remember { mutableStateOf("Oscuro") }
    var ambienteSel by remember { mutableStateOf("Noche") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO BLUR
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(30.dp),
            contentScale = ContentScale.Crop
        )

        // FILTRO NAVY
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A).copy(alpha = 0.60f)))

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState), // Habilitamos scroll para que entren todos los chips
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 4. BOTÓN VOLVER
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

                // 5. TÍTULOS
                Text(
                    text = "Dale forma a tu cuento",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = IBMPlexSans,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Elegí como querés que sea",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 6. PREVISUALIZACIÓN (La subí antes de los chips como estaba en tu idea original)
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto miniatura",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.9f))
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN: GÉNERO ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Género",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = IBMPlexSans,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                        thickness = 1.dp,
                        color = Color(0xFF7ACAFF).copy(alpha = 0.7f)
                    )
                }

                ContextualFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    itemCount = 5
                ) { index ->
                    val opciones = listOf("Aventura", "Misterio", "Fantasía", "Terror", "Ciencia ficción")
                    val texto = opciones[index]

                    BurbujaChip(
                        text = texto,
                        isSelected = generoSel == texto,
                        onClick = { generoSel = texto }
                    )
                }

                // --- SECCIÓN: NARRADOR ---

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Narrador", color = Color.White, fontSize = 18.sp, fontFamily = IBMPlexSans, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp), thickness = 1.dp, color = Color(0xFF7ACAFF).copy(alpha = 0.7f))
                }

                ContextualFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    itemCount = 3
                ) { index ->
                    val opciones = listOf("Primera persona", "Tercera persona", "Omnisciente")
                    val texto = opciones[index]
                    BurbujaChip(text = texto, isSelected = narradorSel == texto, onClick = { narradorSel = texto })
                }

                // --- SECCIÓN: NARRADOR ---

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Tono", color = Color.White, fontSize = 18.sp, fontFamily = IBMPlexSans, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp), thickness = 1.dp, color = Color(0xFF7ACAFF).copy(alpha = 0.7f))
                }

                ContextualFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    itemCount = 3
                ) { index ->
                    val opciones = listOf("Divertido", "Oscuro", "Épico")
                    val texto = opciones[index]
                    BurbujaChip(text = texto, isSelected = tonoSel == texto, onClick = { tonoSel = texto })
                }

                // --- SECCIÓN: AMBIENTE ---

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Ambiente", color = Color.White, fontSize = 18.sp, fontFamily = IBMPlexSans, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp), thickness = 1.dp, color = Color(0xFF7ACAFF).copy(alpha = 0.7f))
                }

                ContextualFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    itemCount = 4
                ) { index ->
                    val opciones = listOf("Día", "Noche", "Futuro", "Antiguo")
                    val texto = opciones[index]
                    BurbujaChip(text = texto, isSelected = ambienteSel == texto, onClick = { ambienteSel = texto })
                }



                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BurbujaChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),

        // --- CAMBIO AQUÍ: Relleno para el estado no seleccionado ---
        color = if (isSelected) {
            Color(0xFF7B61FF) // Violeta si está seleccionado
        } else {
            Color(0xFF1F2A37).copy(alpha = 0.4f) // Relleno sutil del azul 74A9D2
        },

        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF74A9D2).copy(alpha = 0.9f)),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF0F172A) else Color.White,
            fontSize = 14.sp,
            fontFamily = Inter,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
