package com.wilson.burbuja

// --- IMPORTS: Librerías de Android, Compose, Google y Firebase ---
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

@Composable
fun LoginScreen(
    // ¡CAMBIO CLAVE! Ahora onLoginSuccess exige un String (el nombre)
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // --- 1. REFERENCIAS Y ESTADOS ---
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }

    // --- 2. CONFIGURACIÓN DE GOOGLE SIGN-IN ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("274602078486-6pd344j52agqs9svse9ue9d7pi78bt5n.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // --- 3. GESTOR DE RESULTADOS (EL PUENTE) ---
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnCompleteListener { taskAuth ->
                if (taskAuth.isSuccessful) {
                    scope.launch {
                        val nombreUsuario = account.displayName ?: "Usuario"
                        snackbarHostState.showSnackbar("¡Sesión iniciada! Bienvenido, $nombreUsuario")
                        delay(1500)
                        // ¡CAMBIO CLAVE! Enviamos el nombre obtenido al MainActivity
                        onLoginSuccess(nombreUsuario)
                    }
                } else {
                    isLoading = false
                    scope.launch { snackbarHostState.showSnackbar("Error al conectar con Firebase.") }
                }
            }
        } catch (e: ApiException) {
            isLoading = false
            scope.launch { snackbarHostState.showSnackbar("Inicio de sesión cancelado.") }
        }
    }

    // --- 4. COLORES DE IDENTIDAD (TECNOMINIMALISMO) ---
    val navyBg = Color(0xFF1F2A37)
    val celesteIA = Color(0xFF7BCBFF)

    // --- 5. LÓGICA DE ANIMACIÓN INTERACTIVA ---
    var touchPos by remember { mutableStateOf(Offset(-500f, -500f)) }
    var isTouching by remember { mutableStateOf(false) }

    val pulse by rememberInfiniteTransition(label = "Pulse").animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // --- 6. ESTRUCTURA VISUAL (LAYOUT) ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = navyBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            isTouching = event.changes.any { it.pressed }
                            touchPos = event.changes.first().position
                        }
                    }
                }
        ) {
            CampoDeEnfoque(celesteIA, touchPos, isTouching, pulse)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 30.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- CABECERA ---
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "BIENVENIDO",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Lo que ves puede ser una historia",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.weight(1f))

                // --- BOTONES Y FEEDBACK ---
                if (isLoading) {
                    CircularProgressIndicator(
                        color = celesteIA,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            launcher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(65.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Entrar con Google", color = navyBg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            auth.signInAnonymously().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Accediendo como invitado...")
                                        delay(1000)
                                        // ¡CAMBIO CLAVE! Enviamos un nombre genérico para usuarios anónimos
                                        onLoginSuccess("Invitado")
                                    }
                                } else {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(65.dp),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(2.dp, celesteIA)
                    ) {
                        Text("Crear mi historia", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CampoDeEnfoque(color: Color, touchPos: Offset, isTouching: Boolean, pulse: Float) {
    val smoothX by animateFloatAsState(targetValue = if (isTouching) touchPos.x else 540f, label = "X")
    val smoothY by animateFloatAsState(targetValue = if (isTouching) touchPos.y else 700f, label = "Y")

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(smoothX, smoothY)
        val bubbleRadius = size.width * 0.45f * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                center = center,
                radius = bubbleRadius * 1.5f
            )
        )

        val spacing = 55f
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            for (y in 0..size.height.toInt() step spacing.toInt()) {
                val point = Offset(x.toFloat(), y.toFloat())
                val dist = (point - center).getDistance()
                var offsetPoint = point

                if (dist < bubbleRadius) {
                    val factor = (1f - dist / bubbleRadius).pow(2)
                    offsetPoint = point + (point - center) / dist * (factor * 40f)
                }

                drawCircle(
                    color = color.copy(alpha = if (dist < bubbleRadius) 0.5f else 0.1f),
                    radius = if (dist < bubbleRadius) 2f else 1f,
                    center = offsetPoint
                )
            }
        }
    }
}