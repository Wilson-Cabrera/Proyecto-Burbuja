package com.wilson.burbuja

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wilson.burbuja.components.NavegacionLiteral
import com.wilson.burbuja.data.ElevenLabsConfig
import com.wilson.burbuja.ui.theme.BurbujaTheme
import com.wilson.burbuja.ui.theme.LocalThemeState
import com.wilson.burbuja.ui.theme.LocalThemeToggle
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("BurbujaPrefs", Context.MODE_PRIVATE)

        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(prefs.getBoolean("isDarkTheme", systemTheme))
            }

            CompositionLocalProvider(
                LocalThemeState provides isDarkTheme,
                LocalThemeToggle provides {
                    val nuevoTema = !isDarkTheme
                    isDarkTheme = nuevoTema
                    prefs.edit().putBoolean("isDarkTheme", nuevoTema).apply()
                }
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
    val nombreGuardado = remember { prefs.getString("nombreUsuario", null) }

    LaunchedEffect(usuarioFirebase) {
        if (usuarioFirebase != null) {
            prefs.edit().putBoolean("yaVioOnboarding", true).apply()
        }
    }

    val yaVioOnboarding = remember { prefs.getBoolean("yaVioOnboarding", false) }

    val pantallaDeArranque = remember {
        when {
            usuarioFirebase != null || nombreGuardado != null -> "inicio"
            yaVioOnboarding -> "login"
            else -> "onboarding"
        }
    }

    var isProfileMenuVisible by remember { mutableStateOf(false) }

    var nombreUsuario by remember {
        mutableStateOf(
            when {
                nombreGuardado != null -> nombreGuardado
                usuarioFirebase != null && !usuarioFirebase.displayName.isNullOrBlank() -> usuarioFirebase.displayName!!
                else -> "Usuario"
            }
        )
    }

    val upgradeAccountLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                auth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { linkTask ->
                    if (linkTask.isSuccessful) {
                        val user = linkTask.result?.user
                        nombreUsuario = user?.displayName ?: "Usuario"
                        prefs.edit().putString("nombreUsuario", nombreUsuario).apply()

                        Toast.makeText(context, "¡Cuenta vinculada! Tu biblioteca ahora es segura.", Toast.LENGTH_SHORT).show()

                        navController.navigate("guardados") { popUpTo("inicio") }
                    } else {
                        auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
                            if (signInTask.isSuccessful) {
                                val user = signInTask.result?.user
                                nombreUsuario = user?.displayName ?: "Usuario"
                                prefs.edit().putString("nombreUsuario", nombreUsuario).apply()

                                Toast.makeText(context, "Sesión recuperada con éxito.", Toast.LENGTH_SHORT).show()

                                navController.navigate("guardados") { popUpTo("inicio") }
                            } else {
                                Toast.makeText(context, "Error al acceder con Google.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Inicio de sesión cancelado.", Toast.LENGTH_SHORT).show()
        }
    }

    val cerrarSesion = {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(context, gso).signOut()

        prefs.edit()
            .putBoolean("yaVioOnboarding", true)
            .remove("nombreUsuario")
            .apply()

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
                modifier = Modifier.fillMaxSize()
            ) {
                composable("onboarding") {
                    OnboardingBurbuja(onFinish = {
                        navController.navigate("welcome") { popUpTo("onboarding") { inclusive = true } }
                    })
                }

                composable(
                    route = "welcome",
                    enterTransition = { fadeIn(animationSpec = tween(1200)) },
                    exitTransition = { fadeOut(animationSpec = tween(1000)) }
                ) {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            prefs.edit().putBoolean("yaVioOnboarding", true).apply()
                            navController.navigate("login") { popUpTo("welcome") { inclusive = true } }
                        }
                    )
                }

                composable(
                    route = "login",
                    enterTransition = {
                        if (initialState.destination.route == "welcome") {
                            fadeIn(animationSpec = tween(1500)) +
                                    scaleIn(initialScale = 0.95f, animationSpec = tween(1500))
                        } else {
                            fadeIn(animationSpec = tween(800))
                        }
                    }
                ) {
                    LoginScreen(
                        onLoginSuccess = { nombre ->
                            nombreUsuario = nombre
                            prefs.edit()
                                .putBoolean("yaVioOnboarding", true)
                                .putString("nombreUsuario", nombre)
                                .apply()
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
                    var listaGuardados by remember { mutableStateOf<List<StoryData>>(emptyList()) }
                    var estaCargando by remember { mutableStateOf(true) }

                    val eliminarCuento: (StoryData) -> Unit = { cuento ->
                        val db = FirebaseFirestore.getInstance()
                        val storage = FirebaseStorage.getInstance()
                        val usuarioActual = auth.currentUser

                        if (usuarioActual != null && cuento.id.isNotEmpty()) {
                            db.collection("stories").document(cuento.id).delete().addOnSuccessListener {
                                if (cuento.photoUri.contains("firebasestorage")) {
                                    try {
                                        storage.getReferenceFromUrl(cuento.photoUri).delete()
                                    } catch (e: Exception) {
                                        println("Burbuja Debug: URL de imagen no válida para Storage.")
                                    }
                                }
                                listaGuardados = listaGuardados.filter { it.id != cuento.id }
                                Toast.makeText(context, "Burbuja eliminada", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener { e ->
                                Toast.makeText(context, "Error al eliminar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    LaunchedEffect(usuarioFirebase) {
                        if (usuarioFirebase != null) {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("stories").whereEqualTo("userId", usuarioFirebase.uid).get().addOnSuccessListener { documentos ->
                                val historiasFetch = documentos.map { doc ->
                                    val firebaseTimestamp = doc.getTimestamp("timestamp")
                                    val formatoFecha = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                                    val fechaLegible = firebaseTimestamp?.toDate()?.let { formatoFecha.format(it) } ?: "Reciente"

                                    StoryData(
                                        id = doc.getString("id") ?: doc.id,
                                        title = doc.getString("title") ?: "Sin título",
                                        resultStory = doc.getString("resultStory") ?: "",
                                        photoUri = doc.getString("photoUri") ?: "",
                                        genero = doc.getString("genero") ?: "",
                                        tono = doc.getString("tono") ?: "",
                                        epoca = doc.getString("epoca") ?: "",
                                        fecha = fechaLegible
                                    )
                                }
                                listaGuardados = historiasFetch.reversed()
                                estaCargando = false
                            }.addOnFailureListener { estaCargando = false }
                        } else {
                            estaCargando = false
                        }
                    }

                    Box(Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
                        if (estaCargando) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            GuardadosScreen(
                                listaHistorias = listaGuardados,
                                onDeleteStory = eliminarCuento,
                                onNavigateToCreate = {
                                    navController.navigate("inicio") { popUpTo("inicio") { inclusive = true } }
                                },
                                // --- NUEVO: Clic en la tarjeta para leer ---
                                onStoryClick = { cuento ->
                                    // Metemos el cuento en la mochila de la navegación y vamos a result_screen
                                    navController.currentBackStackEntry?.savedStateHandle?.set("storyData", cuento)
                                    navController.navigate("result_screen")
                                }
                            )
                        }
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

                    // --- NUEVO: Rescatamos el cuento si venimos desde GuardadosScreen ---
                    val savedStory = navController.previousBackStackEntry?.savedStateHandle?.get<StoryData>("storyData")

                    val storyData = savedStory ?: if (uiState is StoryState.Success) {
                        StoryData(
                            title = uiState.title,
                            resultStory = uiState.story
                        )
                    } else { StoryData() }

                    // Si el id no está vacío, sabemos que estamos leyendo un cuento ya guardado de la base de datos
                    val isAlreadySaved = storyData.id.isNotEmpty()

                    ResultScreen(
                        storyData = storyData,
                        nombreUsuario = nombreUsuario,
                        isAudioLoading = storyViewModel.isAudioLoading,
                        isPlaying = storyViewModel.isPlaying,
                        audioAmplitude = storyViewModel.audioAmplitude,
                        isAnonymous = FirebaseAuth.getInstance().currentUser?.isAnonymous == true,
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
                        onLogout = cerrarSesion,

                        onRealSaveClick = {
                            // --- NUEVA LÓGICA DE GUARDADO ---
                            if (isAlreadySaved) {
                                // Si ya está guardado, solo tiramos un mensajito
                                Toast.makeText(context, "Esta burbuja ya está a salvo en tu biblioteca.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Si es un cuento recién creado, lo subimos a la nube
                                val usuarioActual = auth.currentUser
                                if (usuarioActual != null && storyData.resultStory.isNotEmpty()) {
                                    Toast.makeText(context, "Guardando en tu biblioteca...", Toast.LENGTH_SHORT).show()

                                    val userId = usuarioActual.uid
                                    val db = FirebaseFirestore.getInstance()

                                    val storyRef = db.collection("stories").document()
                                    val storyId = storyRef.id

                                    val storageRef = FirebaseStorage.getInstance().reference.child("stories/$userId/$storyId.jpg")
                                    val fotoUriLocal = Uri.parse(storyData.photoUri)

                                    storageRef.putFile(fotoUriLocal).addOnSuccessListener {
                                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                            val mapaCuento = hashMapOf(
                                                "id" to storyId,
                                                "userId" to userId,
                                                "title" to storyData.title.ifEmpty { "Fragmentos de Realidad" },
                                                "resultStory" to storyData.resultStory,
                                                "photoUri" to downloadUrl.toString(),
                                                "genero" to storyData.genero,
                                                "tono" to storyData.tono,
                                                "epoca" to storyData.epoca,
                                                "timestamp" to com.google.firebase.Timestamp.now()
                                            )
                                            storyRef.set(mapaCuento).addOnSuccessListener {
                                                Toast.makeText(context, "¡Relato asegurado con éxito!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("guardados") { popUpTo("inicio") }
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(context, "Error al guardar el texto: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(context, "Error al subir la imagen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "No hay un usuario activo o el relato está vacío.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },

                        onUpgradeAccountClick = {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(context.getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)

                            googleSignInClient.signOut().addOnCompleteListener {
                                upgradeAccountLauncher.launch(googleSignInClient.signInIntent)
                            }
                        }
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

// ... (El resto de tus composables OnboardingBurbuja, PantallaInicio, etc., quedan igual abajo) ...
@Composable
fun OnboardingBurbuja(onFinish: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.intro_burbuja))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = 1)
    LaunchedEffect(progress) { if (progress == 1f) onFinish() }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1F2A37)), contentAlignment = Alignment.Center) {
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
            Text("¿Qué historia hay a tu alrededor?", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 40.dp))
            BotonCamaraPrincipal(onClick = onAbrirCamara)
        }
    }
}

@Composable
fun BotonCamaraPrincipal(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Icon(Icons.Default.CameraAlt, null)
        Spacer(Modifier.width(12.dp))
        Text("Abrir la cámara", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileMenuCard(nombreUsuario: String, onClose: () -> Unit, onLogout: () -> Unit) {
    val isDarkTheme = LocalThemeState.current
    val toggleTheme = LocalThemeToggle.current
    Card(modifier = Modifier.width(260.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hola, $nombreUsuario", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 14.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(16.dp)) }
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