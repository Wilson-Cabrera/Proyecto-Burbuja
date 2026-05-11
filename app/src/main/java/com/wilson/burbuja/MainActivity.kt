package com.wilson.burbuja

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.airbnb.lottie.compose.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.wilson.burbuja.data.ElevenLabsConfig
import com.wilson.burbuja.ui.theme.BurbujaTheme
import com.wilson.burbuja.ui.theme.LocalThemeState
import com.wilson.burbuja.ui.theme.LocalThemeToggle
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            CompositionLocalProvider(
                LocalThemeState provides isDarkTheme,
                LocalThemeToggle provides { isDarkTheme = !isDarkTheme }
            ) {
                BurbujaTheme(darkTheme = isDarkTheme) {
                    val backgroundColor = MaterialTheme.colorScheme.background
                    LaunchedEffect(backgroundColor) {
                        window.decorView.setBackgroundColor(backgroundColor.toArgb())
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = backgroundColor
                    ) {
                        MainScreen()
                    }
                }
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

    val storyViewModel: StoryViewModel = viewModel()
    val auth = remember { FirebaseAuth.getInstance() }
    val usuarioFirebase = auth.currentUser

    var tienePermiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    val prefs = remember { context.getSharedPreferences("BurbujaPrefs", Context.MODE_PRIVATE) }

    LaunchedEffect(usuarioFirebase) {
        if (usuarioFirebase != null) {
            prefs.edit().putBoolean("yaVioOnboarding", true).apply()
        }
    }

    val yaVioOnboarding = remember { prefs.getBoolean("yaVioOnboarding", false) }

    val pantallaDeArranque = remember {
        when {
            usuarioFirebase != null -> "inicio"
            yaVioOnboarding -> "login"
            else -> "onboarding"
        }
    }

    var isProfileMenuVisible by remember { mutableStateOf(false) }
    var nombreUsuario by remember {
        mutableStateOf(
            when {
                usuarioFirebase == null -> "Usuario"
                !usuarioFirebase.displayName.isNullOrBlank() -> usuarioFirebase.displayName!!
                else -> "Usuario"
            }
        )
    }

    val cerrarSesion = {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(context, gso).signOut()
        prefs.edit().putBoolean("yaVioOnboarding", true).apply()
        nombreUsuario = "Usuario"
        isProfileMenuVisible = false
        navController.navigate("login") {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.blur(if (isProfileMenuVisible) 20.dp else 0.dp),
            containerColor = Color.Transparent,
            bottomBar = {
                AnimatedVisibility(visible = rutaActual in listOf("inicio", "guardados")) {
                    NavegacionLiteral(
                        navController = navController,
                        letraUsuario = nombreUsuario.firstOrNull()?.toString()?.uppercase() ?: "U",
                        onProfileClick = { isProfileMenuVisible = !isProfileMenuVisible }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = pantallaDeArranque,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                // 1. PANTALLA DE ANIMACIÓN LOTTIE
                composable("onboarding") {
                    OnboardingBurbuja(onFinish = {
                        // Navegación directa al puente cinematográfico
                        navController.navigate("welcome") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    })
                }

                // 2. PANTALLA PUENTE (WelcomeScreen) - CON SALIDA FLUIDA
                composable(
                    route = "welcome",
                    enterTransition = {
                        fadeIn(animationSpec = tween(1200))
                    },
                    exitTransition = {
                        // Se desvanece suavemente al salir hacia el login
                        fadeOut(animationSpec = tween(1000))
                    }
                ) {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            prefs.edit().putBoolean("yaVioOnboarding", true).apply()
                            navController.navigate("login") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }

                // 3. PANTALLA DE LOGIN - CON ENTRADA CINEMATOGRÁFICA
                composable(
                    route = "login",
                    enterTransition = {
                        // Solo hace la animación especial si viene desde la WelcomeScreen
                        if (initialState.destination.route == "welcome") {
                            fadeIn(animationSpec = tween(1500)) +
                                    scaleIn(initialScale = 0.95f, animationSpec = tween(1500))
                        } else {
                            EnterTransition.None
                        }
                    }
                ) {
                    LoginScreen(
                        onLoginSuccess = { nombre ->
                            nombreUsuario = nombre
                            prefs.edit().putBoolean("yaVioOnboarding", true).apply()
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

                composable("guardados") {
                    Box(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
                        PantallaGuardados()
                    }
                }

                composable("camara") { CameraScreen(navController, onBackClicked = { navController.popBackStack() }) }

                composable("preview_screen/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    PreviewScreen(navController, uri)
                }

                composable("story_configuration/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    StoryConfigurationScreen(navController, uri, storyViewModel) { navController.popBackStack() }
                }

                composable("loading/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    LoadingScreen(navController, uri, storyViewModel) {
                        navController.navigate("result_screen") { popUpTo("loading/{photoUri}") { inclusive = true } }
                    }
                }

                composable("result_screen") {
                    val uiState = storyViewModel.uiState
                    val storyData = if (uiState is StoryState.Success) {
                        navController.previousBackStackEntry?.savedStateHandle?.get<StoryData>("storyData") ?: StoryData(
                            title = uiState.title,
                            resultStory = uiState.story
                        )
                    } else { StoryData() }

                    ResultScreen(
                        storyData = storyData,
                        nombreUsuario = nombreUsuario,
                        isAudioLoading = storyViewModel.isAudioLoading,
                        isPlaying = storyViewModel.isPlaying,
                        audioAmplitude = storyViewModel.audioAmplitude,
                        onPlayAudioClick = {
                            val idUnico = storyData.resultStory.hashCode().toString()
                            if (storyViewModel.audioFile != null) {
                                storyViewModel.alternarAudio(context, storyViewModel.audioFile!!)
                            } else {
                                storyViewModel.prepararAudio(context, storyData.resultStory, ElevenLabsConfig.VOICE_MISTERIO, idUnico)
                            }
                        },
                        onStopAudioClick = { storyViewModel.detenerAudio() },
                        onBackClick = { storyViewModel.detenerAudio(); navController.popBackStack() },
                        onGenerateAnother = { storyViewModel.detenerAudio(); navController.popBackStack() },
                        onLogout = cerrarSesion
                    )
                }
            }
        }

        if (isProfileMenuVisible) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { isProfileMenuVisible = false })
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 95.dp, end = 20.dp), contentAlignment = Alignment.BottomEnd) {
                ProfileMenuCard(nombreUsuario, onClose = { isProfileMenuVisible = false }, onLogout = cerrarSesion)
            }
        }
    }
}

@Composable
fun OnboardingBurbuja(onFinish: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.intro_burbuja))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = 1)

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onFinish()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1F2A37)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    }
}

@Composable
fun PantallaInicio(onAbrirCamara: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("¿Qué historia hay a tu alrededor?",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            BotonCamaraPrincipal(onClick = onAbrirCamara)
        }
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Default.CameraAlt, null)
        Spacer(Modifier.width(12.dp))
        Text("Abrir la cámara", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaGuardados() { }

@Composable
fun ProfileMenuCard(nombreUsuario: String, onClose: () -> Unit, onLogout: () -> Unit) {
    val isDarkTheme = LocalThemeState.current
    val toggleTheme = LocalThemeToggle.current
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hola, $nombreUsuario", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 14.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem(icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.NightsStay, text = if (isDarkTheme) "Modo claro" else "Modo oscuro", onClick = toggleTheme)
            ProfileMenuItem(Icons.Default.Share, "Compartir") {}
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ProfileMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión", onLogout)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}