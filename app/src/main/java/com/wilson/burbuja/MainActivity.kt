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

    // La barra inferior solo aparece en las secciones principales
    val mostrarBottomBar = rutaActual in listOf("inicio", "galeria", "guardados")

    // --- GESTIÓN DE PERMISOS ---
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
        containerColor = Color(0xFF1F2A37), // Azul Oscuro oficial
        bottomBar = {
            AnimatedVisibility(
                visible = mostrarBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavegacionLiteral(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1F2A37))
        ) {

            // 1. INICIO
            composable("inicio") {
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

            // 2. CÁMARA
            composable(
                route = "camara",
                enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                CameraScreen(navController = navController, onBackClicked = { navController.popBackStack() })
            }

            // 3. PREVIEW: Confirmación de la foto capturada
            composable(
                route = "preview_screen/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                PreviewScreen(navController = navController, photoUri = uri)
            }

            // 4. CONFIGURACIÓN: Donde el usuario elige el estilo del cuento
            composable(
                route = "story_configuration/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                StoryConfigurationScreen(
                    navController = navController,
                    photoUri = uri,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 5. CARGA: La animación de escaneo tecno
            composable(
                route = "loading/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                LoadingScreen(
                    photoUri = uri,
                    onLoadingFinished = {
                        // Navegamos al resultado y limpiamos la carga del historial
                        navController.navigate("result_screen") {
                            popUpTo("loading/{photoUri}") { inclusive = true }
                        }
                    }
                )
            }

            // 6. RESULTADO: La pantalla final de lectura
            composable(
                route = "result_screen",
                enterTransition = { fadeIn(tween(700)) }
            ) {
                // Recuperamos el objeto StoryData guardado en el savedStateHandle
                val storyData = remember {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<StoryData>("storyData") ?: StoryData()
                }

                ResultScreen(
                    storyData = storyData,
                    onBackClick = {
                        navController.popBackStack("inicio", inclusive = false)
                    },
                    onGenerateAnother = {
                        navController.popBackStack("story_configuration/{photoUri}", inclusive = false)
                    }
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
            AnimatedVisibility(visible = startAnim, enter = fadeIn(tween(800))) {
                Text(
                    text = "¿Qué historia hay a tu alrededor hoy?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            BotonCamaraPrincipal(onClick = onAbrirCamara)
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
            Text(text = "Abrir la cámara", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable fun PantallaGaleria(p: PaddingValues) { Box(Modifier.fillMaxSize().padding(p).background(Color(0xFF1F2A37))) }
@Composable fun PantallaGuardados(p: PaddingValues) { Box(Modifier.fillMaxSize().padding(p).background(Color(0xFF1F2A37))) }