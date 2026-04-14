package com.wilson.burbuja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wilson.burbuja.ui.theme.BurbujaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BurbujaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color(0xFF1F2A37), // Fondo oscuro tecno
        bottomBar = {
            NavegacionLiteral(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("inicio") { PantallaInicio() }
            composable("galeria") { PantallaGaleria() }
            composable("guardados") { PantallaGuardados() }
        }
    }
}

// --- AQUÍ ESTÁ EL TRUCO: LA PANTALLA DEBE LLAMAR AL BOTÓN ---
@Composable
fun PantallaInicio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Qué historia hay a tu alrededor hoy?",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // LLAMADA AL BOTÓN (Si esto no está, el botón no aparece)
        BotonCamaraPrincipal(onClick = {
            /* Lógica de cámara próximamente */
        })
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color(0xFF7ACAFF)), // Tu celeste tecno
        // Forzamos el contentColor a blanco aquí también por seguridad
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Asegura que el grupo esté centrado
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White // <--- Forzamos el icono a blanco
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Abrir la cámara",
                fontSize = 16.sp,
                color = Color.White // <--- Forzamos el texto a blanco
            )
        }
    }
}

@Composable
fun PantallaGaleria() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Galería de renders", color = Color.White)
    }
}

@Composable
fun PantallaGuardados() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Historias favoritas", color = Color.White)
    }
}