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
    onLoginSuccess: () -> Unit,      // Callback para ir a la Home
    onNavigateToRegister: () -> Unit // Callback por si necesitas registro extra
) {
    // --- 1. REFERENCIAS Y ESTADOS ---
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() } // Instancia de Firebase Auth
    val scope = rememberCoroutineScope()               // Para lanzar procesos asíncronos (corrutinas)
    val snackbarHostState = remember { SnackbarHostState() } // Para mostrar carteles de aviso

    // Estado para controlar si la app está trabajando (mostrando el circulito de carga)
    var isLoading by remember { mutableStateOf(false) }

    // --- 2. CONFIGURACIÓN DE GOOGLE SIGN-IN ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("274602078486-6pd344j52agqs9svse9ue9d7pi78bt5n.apps.googleusercontent.com") // ID de tu consola Firebase
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // --- 3. GESTOR DE RESULTADOS (EL PUENTE) ---
    // Este bloque se ejecuta cuando volvés de la ventanita de Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Intentamos loguear en Firebase con la cuenta de Google obtenida
            auth.signInWithCredential(credential).addOnCompleteListener { taskAuth ->
                if (taskAuth.isSuccessful) {
                    scope.launch {
                        // Mostramos mensaje de éxito con el nombre del usuario
                        snackbarHostState.showSnackbar("¡Sesión iniciada! Bienvenido, ${account.displayName}")
                        delay(1500) // Tiempo de respiro para que el usuario lea el mensaje
                        onLoginSuccess() // Navegamos a la siguiente pantalla
                    }
                } else {
                    isLoading = false
                    scope.launch { snackbarHostState.showSnackbar("Error al conectar con Firebase.") }
                }
            }
        } catch (e: ApiException) {
            // Si el usuario cierra el selector de Google sin elegir nada
            isLoading = false
            scope.launch { snackbarHostState.showSnackbar("Inicio de sesión cancelado.") }
        }
    }

    // --- 4. COLORES DE IDENTIDAD (TECNOMINIMALISMO) ---
    val navyBg = Color(0xFF1F2A37)
    val celesteIA = Color(0xFF7BCBFF)

    // --- 5. LÓGICA DE ANIMACIÓN INTERACTIVA ---
    var touchPos by remember { mutableStateOf(Offset(-500f, -500f)) } // Posición del dedo
    var isTouching by remember { mutableStateOf(false) }               // ¿Está tocando la pantalla?

    // Animación de pulso infinito para el brillo del fondo
    val pulse by rememberInfiniteTransition(label = "Pulse").animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // --- 6. ESTRUCTURA VISUAL (LAYOUT) ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Contenedor de mensajes flotantes
        containerColor = navyBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Capturamos los movimientos del dedo para la animación del fondo
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
            // Capa del fondo dibujado por código
            CampoDeEnfoque(celesteIA, touchPos, isTouching, pulse)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding() // Evita que el contenido choque con la cámara frontal o bordes
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

                // Resorte que empuja los botones hacia abajo
                Spacer(modifier = Modifier.weight(1f))

                // --- BOTONES Y FEEDBACK ---
                if (isLoading) {
                    // Círculo de carga mientras se procesa el login
                    CircularProgressIndicator(
                        color = celesteIA,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                } else {
                    // Botón principal: Google
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

                    // Botón secundario: Invitado (Login Anónimo)
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            auth.signInAnonymously().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Accediendo como invitado...")
                                        delay(1000)
                                        onLoginSuccess()
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

/**
 * Función encargada de dibujar el fondo de puntos interactivo mediante un Canvas.
 * Usa cálculos matemáticos para el efecto de deformación al tocar.
 */
@Composable
fun CampoDeEnfoque(color: Color, touchPos: Offset, isTouching: Boolean, pulse: Float) {
    // Suavizado del movimiento de la "burbuja" de enfoque
    val smoothX by animateFloatAsState(targetValue = if (isTouching) touchPos.x else 540f, label = "X")
    val smoothY by animateFloatAsState(targetValue = if (isTouching) touchPos.y else 700f, label = "Y")

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(smoothX, smoothY)
        val bubbleRadius = size.width * 0.45f * pulse

        // Dibujamos el resplandor radial
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                center = center,
                radius = bubbleRadius * 1.5f
            )
        )

        // Dibujamos la cuadrícula de puntos interactivos
        val spacing = 55f // Espacio entre puntos
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            for (y in 0..size.height.toInt() step spacing.toInt()) {
                val point = Offset(x.toFloat(), y.toFloat())
                val dist = (point - center).getDistance()
                var offsetPoint = point

                // Si el punto está dentro del radio de la burbuja, lo "empujamos"
                if (dist < bubbleRadius) {
                    val factor = (1f - dist / bubbleRadius).pow(2)
                    offsetPoint = point + (point - center) / dist * (factor * 40f)
                }

                // Dibujamos el punto individualmente
                drawCircle(
                    color = color.copy(alpha = if (dist < bubbleRadius) 0.5f else 0.1f),
                    radius = if (dist < bubbleRadius) 2f else 1f,
                    center = offsetPoint
                )
            }
        }
    }
}