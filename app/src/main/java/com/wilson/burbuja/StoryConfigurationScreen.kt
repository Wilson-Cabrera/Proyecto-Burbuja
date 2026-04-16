package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

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
                .background(Color(0xFF0F172A).copy(alpha = 0.75f))
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
                    .padding(horizontal = 24.dp) // Margen lateral de diseño
            ) {

                // 4. BOTÓN VOLVER: Icono minimalista blanco
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(top = 8.dp)
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Elegí como querés que sea",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light // Acá usarás Inter
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. PREVISUALIZACIÓN: La foto limpia en una "tarjeta" redondeada
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto miniatura",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp)), // Bordes suaves como en Figma
                    contentScale = ContentScale.Crop
                )

                // --- ESPACIO PARA LOS CHIPS (Lo que sigue...) ---
                // Mañana o en el siguiente paso agregamos los selectores aquí abajo
            }
        }
    }
}