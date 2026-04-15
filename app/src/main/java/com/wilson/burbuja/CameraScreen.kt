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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var zoomPercentage by remember { mutableFloatStateOf(0f) }
    var mostrarIndicadorZoom by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var triggerScanner by remember { mutableStateOf(false) }

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
            navController.navigate("preview_screen/$encodedUri")
        }
    }

    LaunchedEffect(lensFacing, flashMode) {
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        imageCapture.flashMode = flashMode
        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            cameraControl = camera.cameraControl
        } catch (e: Exception) { Log.e("BURBUJA", "Error: ${e.message}") }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    preview.setSurfaceProvider(surfaceProvider)
                    val detector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(d: ScaleGestureDetector): Boolean {
                            zoomPercentage = (zoomPercentage + (d.scaleFactor - 1f)).coerceIn(0f, 1f)
                            cameraControl?.setLinearZoom(zoomPercentage)
                            mostrarIndicadorZoom = true
                            return true
                        }
                    })
                    setOnTouchListener { v, e ->
                        detector.onTouchEvent(e)
                        if (e.action == MotionEvent.ACTION_DOWN && e.pointerCount == 1) {
                            v.performClick()
                            focusPoint = Offset(e.x, e.y)
                            cameraControl?.startFocusAndMetering(FocusMeteringAction.Builder(meteringPointFactory.createPoint(e.x, e.y)).build())
                        }
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
                    center = center,
                    radius = size.maxDimension / 1.5f
                )
            )
        }

        focusPoint?.let { FocusRing(it) { focusPoint = null } }

        AnimatedVisibility(visible = !triggerScanner, enter = fadeIn(), exit = fadeOut()) {
            VisorMinimalistaOverlay()
        }

        ZoomIndicator(zoom = zoomPercentage, visible = mostrarIndicadorZoom)

        // SCANNER CON PARTÍCULAS CIRCULARES (SOFT)
        EfectoScannerPro(trigger = triggerScanner) { triggerScanner = false }

        Column(modifier = Modifier.fillMaxSize()) {
            CameraTopBar(
                onBackClicked = onBackClicked,
                flashMode = flashMode,
                triggerScanner = triggerScanner,
                fraseNarrativa = fraseSeleccionada,
                onFlashToggle = {
                    flashMode = when (flashMode) {
                        ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                        else -> ImageCapture.FLASH_MODE_OFF
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            CameraBottomControls(
                onCaptureClick = {
                    scope.launch {
                        triggerScanner = true
                        delay(650)
                        tomarFotoTemporal(context, imageCapture) { uri ->
                            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                            navController.navigate("preview_screen/$encodedUri")
                        }
                    }
                },
                onGalleryClick = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onSwitchCameraClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                }
            )
        }
    }
}

@Composable
fun EfectoScannerPro(trigger: Boolean, onFinished: () -> Unit) {
    val colorScanner = Color(0xFF7ACAFF)
    val scanProgress = remember { Animatable(0f) }
    val alphaEfecto = remember { Animatable(0f) }

    // Lista de partículas para recuperar la magia
    val particulas = remember {
        List(40) { Offset(Random.nextFloat(), Random.nextFloat() * 0.14f - 0.07f) }
    }

    LaunchedEffect(trigger) {
        if (trigger) {
            launch {
                alphaEfecto.animateTo(1f, tween(100))
                delay(450)
                alphaEfecto.animateTo(0f, tween(200))
            }
            scanProgress.animateTo(1f, tween(700, easing = LinearEasing))
            scanProgress.snapTo(0f)
            onFinished()
        }
    }

    if (trigger) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val currentY = h * scanProgress.value

            // 1. Línea principal
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(colorScanner.copy(alpha = 0f), colorScanner, colorScanner.copy(alpha = 0f)),
                    startY = currentY - 15.dp.toPx(),
                    endY = currentY + 15.dp.toPx()
                ),
                start = Offset(0f, currentY),
                end = Offset(w, currentY),
                strokeWidth = 2.dp.toPx()
            )

            // 2. RECUPERAMOS LAS PARTÍCULAS (Ahora son círculos pequeños)
            particulas.forEach { p ->
                val px = p.x * w
                val py = currentY + (p.y * h * 0.4f)

                drawCircle(
                    color = colorScanner,
                    center = Offset(px, py),
                    radius = Random.nextInt(1, 3).dp.toPx(), // Círculos muy chiquitos
                    alpha = (Random.nextFloat() * 0.6f + 0.2f) * alphaEfecto.value
                )
            }

            // 3. Tinte superior
            drawRect(
                color = colorScanner.copy(alpha = 0.1f * alphaEfecto.value),
                size = Size(w, currentY)
            )
        }
    }
}

@Composable
fun CameraTopBar(onBackClicked: () -> Unit, flashMode: Int, triggerScanner: Boolean, fraseNarrativa: String, onFlashToggle: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(24.dp)).background(Color(0xFF1F2A37).copy(alpha = 0.7f))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (triggerScanner) "ANALIZANDO..." else fraseNarrativa.uppercase(),
                color = if (triggerScanner) Color(0xFF7ACAFF) else Color.White,
                fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onFlashToggle) {
                val icon = when (flashMode) {
                    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                    ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                    else -> Icons.Default.FlashOff
                }
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun CameraBottomControls(onCaptureClick: () -> Unit, onGalleryClick: () -> Unit, onSwitchCameraClick: () -> Unit) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(targetValue = rotationAngle, animationSpec = tween(500), label = "")

    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
            .clip(RoundedCornerShape(32.dp)).background(Color(0xFF1F2A37).copy(alpha = 0.0f))
            .border(0.5.dp, Color.White.copy(alpha = 0.0f), RoundedCornerShape(32.dp)).padding(vertical = 20.dp, horizontal = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape).clickable { onGalleryClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Box(modifier = Modifier.size(72.dp).border(3.dp, Color.White, CircleShape).padding(6.dp).clip(CircleShape).background(Color(0xFFD25450)).clickable { onCaptureClick() })
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape).clickable { rotationAngle += 180f; onSwitchCameraClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Cached, null, tint = Color.White, modifier = Modifier.rotate(animatedRotation))
            }
        }
    }
}

@Composable
fun ZoomIndicator(zoom: Float, visible: Boolean) {
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 160.dp), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Surface(color = Color.Black.copy(alpha = 0.5f), shape = CircleShape) {
                Text(text = "${"%.1f".format(1f + zoom * 4f)}x", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
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
        val w = size.width; val h = size.height; val arcSize = 80.dp.toPx(); val colorVisor = Color.White.copy(alpha = 0.4f); val pad = 70.dp.toPx()
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