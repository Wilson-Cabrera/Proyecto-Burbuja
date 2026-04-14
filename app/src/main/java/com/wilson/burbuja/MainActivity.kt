package com.wilson.burbuja

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.wilson.burbuja.ui.theme.BurbujaTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
    val context = LocalContext.current

    // Lógica para saber en qué pantalla estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // Decidimos si mostrar la barra de navegación (solo en las principales)
    val mostrarBottomBar = rutaActual in listOf("inicio", "galeria", "guardados")

    // --- PERMISOS ---
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    Scaffold(
        containerColor = Color(0xFF1F2A37),
        bottomBar = {
            if (mostrarBottomBar) {
                // Aquí llamamos a tu componente de navegación externa
                NavegacionLiteral(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            // Si no hay barra, usamos padding 0 para que la cámara sea Full Screen
            modifier = Modifier.padding(if (mostrarBottomBar) paddingValues else PaddingValues(0.dp))
        ) {
            // 1. INICIO
            composable("inicio") {
                PantallaInicio(
                    onAbrirCamara = {
                        if (tienePermiso) {
                            navController.navigate("camara")
                        } else {
                            launcher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
            }

            // 2. GALERÍA
            composable("galeria") { PantallaGaleria() }

            // 3. GUARDADOS
            composable("guardados") { PantallaGuardados() }

            // 4. CÁMARA (Full Screen)
            composable("camara") {
                CameraScreen(
                    navController = navController,
                    onBackClicked = { navController.popBackStack() }
                )
            }

            // 5. PREVIEW (Recibe la foto para confirmar)
            composable(
                route = "preview_screen/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                val decodedUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())

                PreviewScreen(
                    navController = navController,
                    photoUri = decodedUri
                )
            }
        }
    }
}

// --- PANTALLAS RECUPERADAS (Tus diseños de Figma) ---

@Composable
fun PantallaInicio(onAbrirCamara: () -> Unit) {
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

        // ACÁ ESTÁ EL CAMBIO: Llamamos a la función que declaraste abajo
        BotonCamaraPrincipal(onClick = onAbrirCamara)
    }
}

@Composable
fun PantallaGaleria() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Galería de cuentos", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun PantallaGuardados() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Historias favoritas", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Más alto para que el texto no se corte
        shape = CircleShape,
        border = BorderStroke(2.dp, Color(0xFF7ACAFF)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho del botón
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // ESTO centra el pack Icono + Texto
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Abrir la cámara",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}