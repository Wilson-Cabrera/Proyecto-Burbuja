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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun CameraScreen(
    navController: NavController,
    onBackClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var zoomPercentage by remember { mutableFloatStateOf(0f) }

    // ESTADO PARA EL FLASH VISUAL
    var mostrarFlash by remember { mutableStateOf(false) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // LANZADOR DE GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val encodedUri = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("preview_screen/$encodedUri")
        }
    }

    val alphaVisor by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.4f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Reverse),
        label = ""
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val detector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(d: ScaleGestureDetector): Boolean {
                        zoomPercentage = (zoomPercentage + (d.scaleFactor - 1f)).coerceIn(0f, 1f)
                        cameraControl?.setLinearZoom(zoomPercentage)
                        return true
                    }
                })
                cameraProviderFuture.addListener({
                    val provider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        provider.unbindAll()
                        val camera = provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                        cameraControl = camera.cameraControl
                    } catch (e: Exception) { Log.e("BURBUJA", "Error: ${e.message}") }
                }, ContextCompat.getMainExecutor(ctx))

                previewView.setOnTouchListener { v, e ->
                    detector.onTouchEvent(e)
                    if (e.action == MotionEvent.ACTION_DOWN && e.pointerCount == 1) {
                        v.performClick()
                        focusPoint = Offset(e.x, e.y)
                        cameraControl?.startFocusAndMetering(
                            FocusMeteringAction.Builder(
                                previewView.meteringPointFactory.createPoint(e.x, e.y)
                            ).build()
                        )
                    }
                    true
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        focusPoint?.let { FocusRing(it) { focusPoint = null } }
        VisorMinimalistaOverlay(alphaVisor)

        // CAPA DE FLASH VISUAL (Feedback Multimedia)
        AnimatedVisibility(
            visible = mostrarFlash,
            enter = fadeIn(animationSpec = tween(50)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }

        Column(modifier = Modifier.fillMaxSize()) {
            CameraTopBar(onBackClicked)
            Spacer(modifier = Modifier.weight(1f))
            CameraBottomControls(
                onCaptureClick = {
                    scope.launch {
                        mostrarFlash = true
                        delay(100)
                        mostrarFlash = false
                        tomarFotoTemporal(context, imageCapture) { uri ->
                            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                            navController.navigate("preview_screen/$encodedUri")
                        }
                    }
                },
                onGalleryClick = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
        }
    }
}

// --- FUNCIONES DE SOPORTE (LAS QUE FALTABAN) ---

@Composable
fun FocusRing(offset: Offset, onFinished: () -> Unit) {
    val scale = remember { Animatable(1.5f) }
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(300))
        delay(600)
        alpha.animateTo(0f, animationSpec = tween(300))
        onFinished()
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color(0xFF7ACAFF),
            radius = 40.dp.toPx() * scale.value,
            center = offset,
            style = Stroke(width = 2.dp.toPx()),
            alpha = alpha.value
        )
    }
}

@Composable
fun VisorMinimalistaOverlay(alphaVisor: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sizeW = size.width
        val sizeH = size.height
        val strokeWidth = 2.dp.toPx()
        val arcSize = 80.dp.toPx()
        val cornerSize = Size(arcSize, arcSize)
        val colorVisor = Color.White.copy(alpha = alphaVisor)
        val padding = 70.dp.toPx()
        val topOffset = 100.dp.toPx()
        val bottomOffset = 150.dp.toPx()

        drawArc(
            color = colorVisor, startAngle = 180f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(padding, padding + topOffset), size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = colorVisor, startAngle = 270f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(sizeW - padding - arcSize, padding + topOffset), size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = colorVisor, startAngle = 90f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(padding, sizeH - padding - arcSize - bottomOffset), size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = colorVisor, startAngle = 0f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(sizeW - padding - arcSize, sizeH - padding - arcSize - bottomOffset), size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun CameraTopBar(onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
        }
        Text(
            text = "¿Y si esto fuera un cuento?",
            color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(end = 48.dp)
        )
    }
}

@Composable
fun CameraBottomControls(onCaptureClick: () -> Unit, onGalleryClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(50.dp).border(1.dp, Color.White, CircleShape).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)).clickable { onGalleryClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
        }

        Box(
            modifier = Modifier.size(80.dp).border(4.dp, Color.White, CircleShape).padding(6.dp)
                .clip(CircleShape).background(Color(0xFFD25450)).clickable { onCaptureClick() }
        )

        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Cached, contentDescription = null, tint = Color.White)
        }
    }
}

fun tomarFotoTemporal(context: android.content.Context, imageCapture: ImageCapture, onResult: (Uri) -> Unit) {
    val photoFile = File(context.cacheDir, "temp_burbuja_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onResult(outputFileResults.savedUri ?: Uri.fromFile(photoFile))
            }
            override fun onError(e: ImageCaptureException) { Log.e("BURBUJA", "Error: ${e.message}") }
        }
    )
}