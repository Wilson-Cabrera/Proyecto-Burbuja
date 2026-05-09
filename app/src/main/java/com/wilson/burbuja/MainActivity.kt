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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme // NUEVO IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode // NUEVO IMPORT
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.wilson.burbuja.data.ElevenLabsConfig
import com.wilson.burbuja.ui.theme.BurbujaTheme
import com.wilson.burbuja.ui.theme.LocalThemeState // NUEVO IMPORT
import com.wilson.burbuja.ui.theme.LocalThemeToggle // NUEVO IMPORT
import java.net.URLDecoder


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Leemos el tema del sistema la primera vez
            val systemTheme = isSystemInDarkTheme()
            // 2. Creamos un estado que podemos modificar con el botón
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            // 3. Encendemos el teletransportador para que el botón pueda acceder a 'isDarkTheme'
            CompositionLocalProvider(
                LocalThemeState provides isDarkTheme,
                LocalThemeToggle provides { isDarkTheme = !isDarkTheme }
            ) {
                // 4. Le pasamos el estado forzado a tu tema
                BurbujaTheme(darkTheme = isDarkTheme) {
                    MainScreen()
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

    val cerrarSesion = {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        googleClient.signOut()
        nombreUsuario = "Usuario"
        isProfileMenuVisible = false
        storyViewModel.detenerAudio()
        navController.navigate("login") { popUpTo(0) { inclusive = true } }
    }

    val mostrarBottomBar = rutaActual in listOf("inicio", "guardados")

    var tienePermiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido -> tienePermiso = concedido }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.blur(if (isProfileMenuVisible || isProcessingImage) 20.dp else 0.dp),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = mostrarBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    NavegacionLiteral(
                        navController = navController,
                        letraUsuario = letraUsuario,
                        onProfileClick = { isProfileMenuVisible = !isProfileMenuVisible }
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
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())) {
                        PantallaInicio(
                            onAbrirCamara = {
                                if (tienePermiso) navController.navigate("camara")
                                else launcherCamara.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }
                }

                composable("guardados") {
                    Box(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
                        PantallaGuardados()
                    }
                }

                composable("camara") {
                    CameraScreen(navController, onBackClicked = { navController.popBackStack() })
                }

                composable("preview_screen/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    PreviewScreen(navController, uri)
                }

                composable("story_configuration/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    StoryConfigurationScreen(
                        navController = navController,
                        photoUri = uri,
                        viewModel = storyViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("loading/{photoUri}") { backStackEntry ->
                    val uri = URLDecoder.decode(backStackEntry.arguments?.getString("photoUri") ?: "", "UTF-8")
                    LoadingScreen(
                        navController = navController,
                        photoUri = uri,
                        viewModel = storyViewModel,
                        onLoadingFinished = {
                            navController.navigate("result_screen") { popUpTo("loading/{photoUri}") { inclusive = true } }
                        }
                    )
                }

                composable("result_screen") {
                    val uiState = storyViewModel.uiState
                    val storyData = if (uiState is StoryState.Success) {
                        navController.previousBackStackEntry?.savedStateHandle?.get<StoryData>("storyData") ?: StoryData(
                            title = uiState.title,
                            resultStory = uiState.story
                        )
                    } else {
                        StoryData()
                    }

                    LaunchedEffect(storyViewModel.audioErrorMessage) {
                        storyViewModel.audioErrorMessage?.let { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            storyViewModel.clearAudioError()
                        }
                    }

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
                                val voiceId = when(storyData.genero.lowercase()) {
                                    "terror", "suspenso" -> ElevenLabsConfig.VOICE_TERROR
                                    "fantasía", "magia" -> ElevenLabsConfig.VOICE_FANTASIA
                                    else -> ElevenLabsConfig.VOICE_MISTERIO
                                }

                                storyViewModel.prepararAudio(
                                    context = context,
                                    texto = storyData.resultStory,
                                    voiceId = voiceId,
                                    storyId = idUnico
                                )
                            }
                        },
                        onStopAudioClick = {
                            storyViewModel.detenerAudio()
                        },
                        onBackClick = {
                            storyViewModel.detenerAudio()
                            navController.popBackStack()
                        },
                        onGenerateAnother = {
                            storyViewModel.detenerAudio()
                            navController.popBackStack()
                        },
                        onLogout = cerrarSesion
                    )
                }
            }
        }

        AnimatedVisibility(visible = isProfileMenuVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { isProfileMenuVisible = false }
            )
        }

        if (isProfileMenuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 95.dp, end = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                ProfileMenuCard(
                    nombreUsuario = nombreUsuario,
                    onClose = { isProfileMenuVisible = false },
                    onLogout = cerrarSesion
                )
            }
        }
    }
}

// --- COMPONENTES DE PANTALLA ---

@Composable
fun PantallaInicio(onAbrirCamara: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "¿Qué historia hay a tu alrededor?",
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
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text("Abrir la cámara", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaGuardados() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Mis Burbujas Guardadas", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun AICapsuleVoice(
    isPlaying: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "voice_plasma")

    val wave1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (isPlaying) 300 else 2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(if (isPlaying) 450 else 2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(if (isPlaying) 250 else 2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )

    val colorPrimario = MaterialTheme.colorScheme.primary
    val colorSecundario = MaterialTheme.colorScheme.secondary
    val colorFondoCapsula = MaterialTheme.colorScheme.surface
    val colorTextoIcono = MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier
            .width(220.dp)
            .height(64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isLoading,
                onClick = onClick
            ),
        shape = RoundedCornerShape(50),
        color = colorFondoCapsula,
        border = BorderStroke(1.dp, colorPrimario.copy(alpha = if (isPlaying) 0.5f else 0.1f)),
        shadowElevation = if (isPlaying) 12.dp else 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2f
                val w = size.width

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorPrimario.copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(w * 0.25f, centerY),
                        radius = (size.height * 1.2f) * wave1
                    ),
                    center = Offset(w * 0.25f, centerY),
                    radius = (size.height * 1.2f) * wave1
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorSecundario.copy(alpha = 0.9f), Color.Transparent),
                        center = Offset(w * 0.5f, centerY),
                        radius = (size.height * 1.5f) * wave2
                    ),
                    center = Offset(w * 0.5f, centerY),
                    radius = (size.height * 1.5f) * wave2
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorPrimario.copy(alpha = 0.7f), Color.Transparent),
                        center = Offset(w * 0.75f, centerY),
                        radius = (size.height * 1.2f) * wave3
                    ),
                    center = Offset(w * 0.75f, centerY),
                    radius = (size.height * 1.2f) * wave3
                )
            }

            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = colorTextoIcono,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = colorTextoIcono,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = when {
                        isLoading -> "Sintonizando..."
                        isPlaying -> "Burbuja Hablando..."
                        else -> "Escuchar Relato"
                    },
                    color = colorTextoIcono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// --- SUBCOMPONENTES (Perfil) MODIFICADOS PARA EL TEMA ---
@Composable
fun ProfileMenuCard(nombreUsuario: String, onClose: () -> Unit, onLogout: () -> Unit) {
    // RECIBIMOS LA SEÑAL DEL TELETRANSPORTADOR
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

            // BOTÓN CONECTADO AL TEMA DINÁMICO
            ProfileMenuItem(
                icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.NightsStay,
                text = if (isDarkTheme) "Modo claro" else "Modo oscuro",
                onClick = toggleTheme // Acá hace la magia de cambiar todo
            )

            ProfileMenuItem(Icons.Default.Share, "Compartir") {}
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ProfileMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión", onLogout)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}