package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ResultScreen(
    storyData: StoryData,
    onBackClick: () -> Unit,
    onGenerateAnother: () -> Unit
) {
    // Estado para controlar el desplazamiento del texto largo
    val scrollState = rememberScrollState()

    // Usamos Box para encimar elementos (Foto -> Gradiente -> Contenido)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2A37)) // Azul Navy de Burbuja
    ) {

        // --- CAPA 1: LA IMAGEN DE FONDO ---
        AsyncImage(
            model = storyData.photoUri,
            contentDescription = "Foto capturada",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f), // Ocupa un poco más de la mitad para lucirse
            contentScale = ContentScale.Crop
        )

        // --- CAPA 2: EL GRADIENTE (Truco de diseño para legibilidad) ---
        // Va de transparente total a azul sólido para "fundir" la foto con el fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF1F2A37).copy(alpha = 0.5f), // Punto medio de transición
                            Color(0xFF1F2A37) // Azul sólido abajo
                        ),
                        startY = 0f,
                        endY = 1200f // Estiramos el degradado para suavizarlo
                    )
                )
        )

        // --- CAPA 3: ELEMENTOS FIJOS (Botón de volver) ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding() // Respeta el espacio de la batería/reloj
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape) // Fondo sutil para contraste
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        // --- CAPA 4: CONTENIDO SCROLLABLE (Título + Cuento + Botones) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp)
        ) {
            // Espacio vacío para que el texto empiece justo al final de la foto
            Spacer(modifier = Modifier.height(320.dp))

            // TÍTULO DEL CUENTO (Flotando sobre la transición de la foto)
            Text(
                text = "Las aventuras de los ojos viajeros",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // CUERPO DEL CUENTO (El texto generado por la IA)
            Text(
                text = if (storyData.resultStory.isEmpty()) {
                    "Había una vez un fragmento de realidad capturado por una lente mágica... \n\n" +
                            "Aquí es donde la inteligencia artificial de Burbuja escribirá la historia basándose " +
                            "en el género ${storyData.genero} y el tono ${storyData.tono} que elegiste."
                } else {
                    storyData.resultStory
                },
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 17.sp,
                fontFamily = Inter,
                lineHeight = 28.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(60.dp))

            // --- BOTONES INFERIORES (Fieles al diseño Figma) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón "Generar otra versión"
                Button(
                    onClick = onGenerateAnother,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7ACAFF).copy(alpha = 0.15f) // Estilo traslúcido tecno
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF7ACAFF)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generar otra versión",
                        color = Color(0xFF7ACAFF),
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Círculo distintivo "W"
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFF7ACAFF).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFF7ACAFF).copy(alpha = 0.3f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "W",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}