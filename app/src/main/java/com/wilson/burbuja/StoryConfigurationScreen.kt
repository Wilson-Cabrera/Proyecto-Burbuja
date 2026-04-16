package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun StoryConfigurationScreen(
    navController: NavController,
    photoUri: String,
    onBackClick: () -> Unit = {}
) {
    // --- CAPA BASE: EL CONTENEDOR TOTAL ---
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. FONDO: La foto capturada con desenfoque para dar atmósfera
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(30.dp),
            contentScale = ContentScale.Crop
        )

        // 2. FILTRO NAVY: Tinte azul profundo traslúcido para que el texto resalte
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A).copy(alpha = 0.60f))
        )

        // 3. CAPA DE CONTENIDO: Usamos Scaffold para manejar los márgenes del sistema
        Scaffold(
            containerColor = Color.Transparent, // Mantenemos transparente para ver el fondo
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            // Columna principal con scroll por si el contenido es largo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp), // Margen lateral de diseño
                    horizontalAlignment = Alignment.CenterHorizontally //centrar texto
            ) {

                // 4. BOTÓN VOLVER: Icono minimalista blanco
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(top = 8.dp)
                        .align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. TÍTULOS: Jerarquía visual (Acá usarás IBM Plex Sans)
                Text(
                    text = "Dale forma a tu cuento",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter, // <-- Agregado
                )

                Text(
                    text = "Elegí como querés que sea",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light // Acá usarás Inter

                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. PREVISUALIZACIÓN: La foto limpia en una "tarjeta" redondeada
// 6. PREVISUALIZACIÓN con Shadow
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto miniatura",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        // 1. Agregamos la sombra antes del clip
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            clip = false,
                            spotColor = Color.Black.copy(alpha = 0.9f) // Sombra más dramática
                        )
                        // 2. Recortamos la imagen
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )

                // --- ESPACIO PARA LOS CHIPS (Lo que sigue...) ---
                // Mañana o en el siguiente paso agregamos los selectores aquí abajo
            }
        }
    }
}