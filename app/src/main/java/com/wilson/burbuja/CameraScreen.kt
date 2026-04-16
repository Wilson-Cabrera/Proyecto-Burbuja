package com.wilson.burbuja

import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

// Modelo de partículas
data class ParticulaScanner(
    val xRelativa: Float,
    val yRelativa: Float,
    val tamano: Float,
    val opacidadBase: Float,
    val velocidadFlote: Float
)

@Composable
fun CameraScreen(
    navController: NavController,
    onBackClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val frasesNarrativas = remember {
        listOf(
            "¿Y si esto fuera un cuento?",
            "Capturando pedacitos de magia...",
            "Transformando la realidad...",
            "¿Qué historia se esconde aquí?",
            "Buscando el inicio de un relato..."
        )
    }
    val fraseSeleccionada = remember { frasesNarrativas.random() }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var procesandoCaptura by remember { mutableStateOf(false) }
    var triggerScanner by remember { mutableStateOf(false) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var girandoCamara by remember { mutableStateOf(false) }

    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var mostrarZoomLabel by remember { mutableStateOf(false) }

    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val encodedUri = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            // CORREGIDO: preview -> preview_screen
            navController.navigate("preview_screen/$encodedUri")
        }
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            cameraControl = camera.cameraControl
        } catch (e: Exception) { Log.e("BURBUJA", "Error: ${e.message}") }
    }

    val blurAlpha by animateFloatAsState(targetValue = if (girandoCamara) 0.7f else 1f, label = "blurAlpha")
    val blurScale by animateFloatAsState(targetValue = if (girandoCamara) 1.08f else 1f, label = "blurScale")

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    preview.setSurfaceProvider(surfaceProvider)

                    val detector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(d: ScaleGestureDetector): Boolean {
                            val sensibilidad = 3.5f
                            val delta = (d.scaleFactor - 1f) * sensibilidad
                            val newZoom = (zoomLevel + delta).coerceIn(1f, 5f)

                            if (newZoom != zoomLevel) {
                                zoomLevel = newZoom
                                cameraControl?.setLinearZoom((zoomLevel - 1f) / 4f)
                                mostrarZoomLabel = true
                            }
                            return true
                        }

                        override fun onScaleEnd(detector: ScaleGestureDetector) {
                            scope.launch { delay(1000); mostrarZoomLabel = false }
                        }
                    })

                    setOnTouchListener { v, e ->
                        detector.onTouchEvent(e)
                        if (e.action == MotionEvent.ACTION_DOWN) {
                            v.performClick()
                            focusPoint = Offset(e.x, e.y)
                            cameraControl?.startFocusAndMetering(FocusMeteringAction.Builder(meteringPointFactory.createPoint(e.x, e.y)).build())
                        }
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
                .graphicsLayer { alpha = blurAlpha; scaleX = blurScale; scaleY = blurScale }
                .then(if (girandoCamara) Modifier.blur(15.dp) else Modifier)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = Brush.radialGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))))
        }

        focusPoint?.let { FocusRing(it) { focusPoint = null } }

        AnimatedVisibility(
            visible = mostrarZoomLabel,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF7ACAFF).copy(alpha = 0.6f))
            ) {
                Text(
                    text = "ZOOM ${"%.1f".format(zoomLevel)}x",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            CameraTopBar(onBackClicked, flashMode, triggerScanner, fraseSeleccionada) {
                flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                imageCapture.flashMode = flashMode
            }
            Spacer(modifier = Modifier.weight(1f))
            CameraBottomControls(
                onCaptureClick = {
                    if (procesandoCaptura || girandoCamara) return@CameraBottomControls
                    scope.launch {
                        triggerScanner = true; delay(700); procesandoCaptura = true
                        tomarFotoTemporal(context, imageCapture) { uri ->
                            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                            // CORREGIDO: preview -> preview_screen
                            navController.navigate("preview_screen/$encodedUri")
                            procesandoCaptura = false
                        }
                    }
                },
                onGalleryClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSwitchCameraClick = {
                    scope.launch {
                        girandoCamara = true; delay(100)
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                        delay(400); girandoCamara = false
                    }
                }
            )
        }

        VisorMinimalistaOverlay()
        EfectoScannerPro(trigger = triggerScanner) { triggerScanner = false }

        AnimatedVisibility(visible = procesandoCaptura, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.2f)))
        }
    }
}

@Composable
fun CameraTopBar(onBackClicked: () -> Unit, flashMode: Int, triggerScanner: Boolean, fraseNarrativa: String, onFlashToggle: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(24.dp)).background(Color(0xFF1F2A37).copy(alpha = 0.85f))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = if (triggerScanner) "ANALIZANDO..." else fraseNarrativa.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onFlashToggle) {
                val icon = if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun CameraBottomControls(onCaptureClick: () -> Unit, onGalleryClick: () -> Unit, onSwitchCameraClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
            .clip(RoundedCornerShape(32.dp)).background(Color(0xFF1F2A37).copy(alpha = 0.85f))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)).padding(vertical = 20.dp, horizontal = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape).clickable { onGalleryClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Box(modifier = Modifier.size(72.dp).border(3.dp, Color.White, CircleShape).padding(6.dp).clip(CircleShape).background(Color(0xFFD25450)).clickable { onCaptureClick() })
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape).clickable { onSwitchCameraClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Cached, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun EfectoScannerPro(trigger: Boolean, onFinished: () -> Unit) {
    val colorScanner = Color(0xFF7ACAFF)
    val scanProgress = remember { Animatable(0f) }
    val alphaEfecto = remember { Animatable(0f) }
    val particulas = remember { List(40) { ParticulaScanner(Random.nextFloat(), Random.nextFloat() * 0.14f - 0.07f, Random.nextFloat() * 2f + 1f, Random.nextFloat() * 0.5f + 0.2f, Random.nextFloat() * 15f - 7.5f) } }

    LaunchedEffect(trigger) {
        if (trigger) {
            launch { alphaEfecto.animateTo(1f, tween(150)); delay(450); alphaEfecto.animateTo(0f, tween(250)) }
            scanProgress.animateTo(1f, tween(850, easing = FastOutSlowInEasing))
            scanProgress.snapTo(0f); onFinished()
        }
    }

    if (trigger) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val currentY = size.height * scanProgress.value
            drawLine(colorScanner.copy(alpha = 0.3f * alphaEfecto.value), Offset(0f, currentY), Offset(w, currentY), 5.dp.toPx(), StrokeCap.Round)
            drawLine(colorScanner.copy(alpha = alphaEfecto.value), Offset(0f, currentY), Offset(w, currentY), 2.dp.toPx(), StrokeCap.Round)
            particulas.forEach { p ->
                val px = (p.xRelativa * w) + (p.velocidadFlote * scanProgress.value)
                val py = currentY + (p.yRelativa * size.height)
                drawCircle(colorScanner, p.tamano.dp.toPx(), Offset(px, py), p.opacidadBase * alphaEfecto.value)
            }
        }
    }
}

@Composable
fun FocusRing(offset: Offset, onFinished: () -> Unit) {
    val scale = remember { Animatable(1.5f) }
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, tween(300)); delay(600); alpha.animateTo(0f, tween(300)); onFinished() }
    Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(Color(0xFF7ACAFF), 40.dp.toPx() * scale.value, offset, style = Stroke(2.dp.toPx()), alpha = alpha.value) }
}

@Composable
fun VisorMinimalistaOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height; val arcSize = 80.dp.toPx(); val colorVisor = Color.White.copy(alpha = 0.3f); val pad = 70.dp.toPx()
        drawArc(colorVisor, 180f, 90f, false, Offset(pad, pad + 100.dp.toPx()), Size(arcSize, arcSize), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawArc(colorVisor, 270f, 90f, false, Offset(w - pad - arcSize, pad + 100.dp.toPx()), Size(arcSize, arcSize), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawArc(colorVisor, 90f, 90f, false, Offset(pad, h - pad - arcSize - 150.dp.toPx()), Size(arcSize, arcSize), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawArc(colorVisor, 0f, 90f, false, Offset(w - pad - arcSize, h - pad - arcSize - 150.dp.toPx()), Size(arcSize, arcSize), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
    }
}

fun tomarFotoTemporal(context: android.content.Context, imageCapture: ImageCapture, onResult: (Uri) -> Unit) {
    val photoFile = File(context.cacheDir, "temp_burbuja_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(res: ImageCapture.OutputFileResults) { onResult(res.savedUri ?: Uri.fromFile(photoFile)) }
        override fun onError(e: ImageCaptureException) { Log.e("BURBUJA", "Error: ${e.message}") }
    })
}