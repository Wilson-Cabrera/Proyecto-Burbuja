package com.wilson.burbuja

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import java.net.URLDecoder
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
            // Aplicamos el sistema de diseño (Colores y Tipografías) definido en el Theme
            BurbujaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // --- GESTIÓN DE NAVEGACIÓN ---
    // El navController es el objeto central que maneja el historial y los cambios de pantalla
    val navController = rememberNavController()
    val context = LocalContext.current

    // Obtenemos el estado de la ruta actual para lógica de UI (como la BottomBar)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // Decidimos si mostrar la barra inferior dependiendo de si estamos en las secciones principales
    val mostrarBottomBar = rutaActual in listOf("inicio", "galeria", "guardados")

    // --- GESTIÓN DE PERMISOS ---
    // Verificamos si el usuario ya otorgó acceso a la cámara
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher para solicitar el permiso de forma reactiva en Compose
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    Scaffold(
        containerColor = Color(0xFF1F2A37), // Color base de la identidad visual
        bottomBar = {
            // La barra de navegación inferior aparece con una transición suave
            AnimatedVisibility(
                visible = mostrarBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavegacionLiteral(navController = navController)
            }
        }
    ) { paddingValues ->
        // --- NAVHOST: EL MAPA DE RUTAS ---
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // El padding del Scaffold evita que el contenido tape la barra
                .background(Color(0xFF1F2A37))
        ) {

            // 1. PANTALLAS DE MENÚ PRINCIPAL
            composable("inicio") {
                PantallaInicio(
                    paddingValues = PaddingValues(0.dp),
                    onAbrirCamara = {
                        if (tienePermiso) navController.navigate("camara")
                        else launcher.launch(Manifest.permission.CAMERA)
                    }
                )
            }

            composable("galeria") { PantallaGaleria(PaddingValues(0.dp)) }
            composable("guardados") { PantallaGuardados(PaddingValues(0.dp)) }

            // 2. FLUJO DE CREACIÓN DE HISTORIAS
            composable(
                route = "camara",
                enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                CameraScreen(navController = navController, onBackClicked = { navController.popBackStack() })
            }

            composable(
                route = "preview_screen/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                PreviewScreen(navController = navController, photoUri = uri)
            }

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

            // 3. PANTALLA DE CARGA (PROCESAMIENTO)
            composable(
                route = "loading/{photoUri}",
                arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                LoadingScreen(
                    navController = navController,
                    photoUri = uri,
                    onLoadingFinished = {
                        // REGLA DE ORO: Navegamos al resultado y eliminamos la carga del historial
                        // Esto permite que el botón "Atrás" en el resultado vuelva a la configuración.
                        navController.navigate("result_screen") {
                            popUpTo("loading/{photoUri}") { inclusive = true }
                        }
                    }
                )
            }

            // 4. PANTALLA DE RESULTADO FINAL
            composable(
                route = "result_screen",
                enterTransition = { fadeIn(tween(700)) }
            ) {
                // Recuperamos el objeto StoryData que fue actualizado en la pantalla anterior
                val storyData = remember {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<StoryData>("storyData") ?: StoryData()
                }

                ResultScreen(
                    storyData = storyData,
                    onBackClick = {
                        // Vuelve a la configuración gracias a que quitamos 'loading' del historial
                        navController.popBackStack()
                    },
                    onGenerateAnother = {
                        // Permite al usuario re-ajustar parámetros
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// --- COMPONENTES DE APOYO (RECURSOS QUE FALTABAN) ---

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
            AnimatedVisibility(visible = startAnim, enter = fadeIn(tween(800)) + slideInVertically()) {
                Text(
                    text = "¿Qué historia hay a tu alrededor hoy?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Inter,
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

// Placeholders para evitar errores de referencia en el NavHost
@Composable fun PantallaGaleria(p: PaddingValues) { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }
@Composable fun PantallaGuardados(p: PaddingValues) { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }