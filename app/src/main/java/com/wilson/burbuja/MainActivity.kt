package com.wilson.burbuja

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import java.net.URLDecoder
import java.net.URLEncoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.wilson.burbuja.ui.theme.BurbujaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val scope = rememberCoroutineScope() // Clave para manejar los tiempos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    var isProfileMenuVisible by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }
    val usuarioFirebase = auth.currentUser

    var nombreUsuario by remember {
        mutableStateOf(
            when {
                usuarioFirebase == null -> "Usuario"
                !usuarioFirebase.displayName.isNullOrBlank() -> usuarioFirebase.displayName!!
                usuarioFirebase.isAnonymous -> "Invitado"
                else -> "Usuario"
            }
        )
    }

    val pantallaDeArranque = if (usuarioFirebase != null) "inicio" else "login"
    val letraUsuario = nombreUsuario.firstOrNull()?.toString()?.uppercase() ?: "U"

    // --- LANZADOR DE GALERÍA CORREGIDO ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                // Usamos el scope para manejar la espera
                scope.launch {
                    isProcessingImage = true // 1. Mostramos la carga
                    val encodedUri = URLEncoder.encode(it.toString(), "UTF-8")

                    delay(150) // 2. Breve pausa para que el UI pinte el overlay
                    navController.navigate("story_configuration/$encodedUri")

                    delay(800) // 3. Esperamos a que la navegación realmente suceda
                    isProcessingImage = false // 4. Recién ahí apagamos la carga
                }
            }
        }
    )

    val cerrarSesion = {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        googleClient.signOut()
        nombreUsuario = "Usuario"
        isProfileMenuVisible = false
        navController.navigate("login") { popUpTo(0) { inclusive = true } }
    }

    val mostrarBottomBar = rutaActual in listOf("inicio", "galeria", "guardados")

    var tienePermiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // Aplicamos blur al fondo si hay carga o menú abierto
            modifier = Modifier.blur(if (isProfileMenuVisible || isProcessingImage) 20.dp else 0.dp),
            containerColor = Color(0xFF1F2A37),
            bottomBar = {
                AnimatedVisibility(visible = mostrarBottomBar) {
                    NavegacionLiteral(
                        navController = navController,
                        letraUsuario = letraUsuario,
                        onProfileClick = { isProfileMenuVisible = !isProfileMenuVisible },
                        onGalleryClick = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = pantallaDeArranque,
                modifier = Modifier.fillMaxSize()
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { nombre ->
                            nombreUsuario = nombre
                            navController.navigate("inicio") { popUpTo("login") { inclusive = true } }
                        },
                        onNavigateToRegister = {}
                    )
                }

                composable("inicio") {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
                        PantallaInicio(onAbrirCamara = {
                            if (tienePermiso) navController.navigate("camara")
                            else launcherCamara.launch(Manifest.permission.CAMERA)
                        })
                    }
                }

                composable("galeria") { Box(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) { PantallaGaleria() } }
                composable("guardados") { Box(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) { PantallaGuardados() } }
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
                    val storyData = remember { navController.previousBackStackEntry?.savedStateHandle?.get<StoryData>("storyData") ?: StoryData() }
                    ResultScreen(
                        storyData = storyData,
                        nombreUsuario = nombreUsuario,
                        onBackClick = { navController.popBackStack() },
                        onGenerateAnother = { navController.popBackStack() },
                        onLogout = cerrarSesion
                    )
                }
            }
        }

        // --- CAPA DE CARGA (OVERLAY) ---
        AnimatedVisibility(
            visible = isProcessingImage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1F2A37).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF7ACAFF))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Procesando imagen...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // --- CAPA DE PERFIL ---
        AnimatedVisibility(visible = isProfileMenuVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { isProfileMenuVisible = false })
        }

        if (isProfileMenuVisible) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 95.dp, end = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                ProfileMenuCard(nombreUsuario = nombreUsuario, onClose = { isProfileMenuVisible = false }, onLogout = cerrarSesion)
            }
        }
    }
}

// --- PANTALLAS DE APOYO ---
@Composable
fun PantallaInicio(onAbrirCamara: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("¿Qué historia hay a tu alrededor?", color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 32.dp))
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
        Text("Abrir la cámara", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable fun PantallaGaleria() { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }
@Composable fun PantallaGuardados() { Box(Modifier.fillMaxSize().background(Color(0xFF1F2A37))) }