package com.wilson.burbuja

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
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
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    val mostrarBottomBar = rutaActual in listOf("inicio", "galeria", "guardados")

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
            AnimatedVisibility(
                visible = mostrarBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                // Asegurate de tener este componente creado o placeholder (barra de navegacion)
                NavegacionLiteral(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.fillMaxSize().background(Color(0xFF1F2A37))
        ) {
            // --- INICIO ---
            composable(
                route = "inicio",
                enterTransition = { fadeIn(tween(400)) },
                exitTransition = { fadeOut(tween(400)) }
            ) {
                PantallaInicio(
                    paddingValues = paddingValues,
                    onAbrirCamara = {
                        if (tienePermiso) navController.navigate("camara")
                        else launcher.launch(Manifest.permission.CAMERA)
                    }
                )
            }

            composable("galeria") { PantallaGaleria(paddingValues) }
            composable("guardados") { PantallaGuardados(paddingValues) }

            // --- CÁMARA ---
            composable(
                route = "camara",
                enterTransition = {
                    slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn()
                },
                exitTransition = { fadeOut(tween(500)) }
            ) {
                CameraScreen(
                    navController = navController,
                    onBackClicked = { navController.popBackStack() }
                )
            }

            // --- PREVIEW ---
            composable(
                route = "preview_screen/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType }),
                enterTransition = { fadeIn(tween(500)) },
                exitTransition = { fadeOut(tween(400)) }
            ) { backStackEntry ->
                val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                val decodedUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
                PreviewScreen(navController = navController, photoUri = decodedUri)
            }

            // --- CONFIGURACIÓN DEL CUENTO (NUEVA RUTA) ---
            composable(
                route = "story_configuration/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType }),
                enterTransition = { fadeIn(tween(500)) },
                exitTransition = { fadeOut(tween(500)) }
            ) { backStackEntry ->
                val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                val decodedUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())

                StoryConfigurationScreen(
                    navController = navController,
                    photoUri = decodedUri,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// --- COMPONENTES DE APOYO ---

@Composable
fun PantallaInicio(paddingValues: PaddingValues, onAbrirCamara: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnim = true }
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = startAnim, enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { -20 })) {
                Text(
                    text = "¿Qué historia hay a tu alrededor hoy?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            AnimatedVisibility(visible = startAnim, enter = fadeIn(tween(800, 200)) + slideInVertically(initialOffsetY = { 20 })) {
                BotonCamaraPrincipal(onClick = onAbrirCamara)
            }
        }
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Abrir la cámara", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable fun PantallaGaleria(p: PaddingValues) { Box(Modifier.fillMaxSize().padding(p).background(Color(0xFF1F2A37))) }
@Composable fun PantallaGuardados(p: PaddingValues) { Box(Modifier.fillMaxSize().padding(p).background(Color(0xFF1F2A37))) }