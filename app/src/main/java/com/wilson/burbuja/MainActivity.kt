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
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    Scaffold(
        containerColor = Color(0xFF1F2A37),
        bottomBar = {
            AnimatedVisibility(visible = mostrarBottomBar, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
                NavegacionLiteral(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "inicio", modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            composable("inicio") {
                PantallaInicio(onAbrirCamara = {
                    if (tienePermiso) navController.navigate("camara") else launcher.launch(Manifest.permission.CAMERA)
                })
            }
            composable("galeria") { PantallaGaleria() }
            composable("guardados") { PantallaGuardados() }

            composable("camara") { CameraScreen(navController, onBackClicked = { navController.popBackStack() }) }

            composable("preview_screen/{photoUri}") { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                PreviewScreen(navController, uri)
            }

            composable("story_configuration/{photoUri}") { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                StoryConfigurationScreen(navController, uri, onBackClick = { navController.popBackStack() })
            }

            composable("loading/{photoUri}") { backStackEntry ->
                val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                LoadingScreen(navController, uri, onLoadingFinished = {
                    navController.navigate("result_screen") { popUpTo("loading/{photoUri}") { inclusive = true } }
                })
            }

            composable("result_screen") {
                val storyData = remember {
                    navController.previousBackStackEntry?.savedStateHandle?.get<StoryData>("storyData") ?: StoryData()
                }
                ResultScreen(
                    storyData = storyData,
                    onBackClick = { navController.popBackStack() },
                    onGenerateAnother = { navController.popBackStack() }
                )
            }
        }
    }
}

// --- COMPONENTES DE APOYO ---

@Composable
fun PantallaInicio(onAbrirCamara: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "¿Qué historia hay a tu alrededor?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            BotonCamaraPrincipal(onClick = onAbrirCamara)
        }
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Abrir la cámara",
            color = Color.White, // Forzamos el blanco para que se vea en el emulador
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable fun PantallaGaleria() { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }
@Composable fun PantallaGuardados() { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }