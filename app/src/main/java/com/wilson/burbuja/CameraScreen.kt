package com.wilson.burbuja

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

@Composable
fun CameraScreen(onBackClicked: () -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }

    // Estado para ver el porcentaje de zoom en pantalla
    var zoomPercentage by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val alphaAnimada by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
        label = "AlphaPulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // CAPA 0: Cámara con Zoom y Enfoque integrados
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                // 1. GESTOR DE ZOOM (Pinch/Pellizco)
                val scaleGestureDetector = ScaleGestureDetector(ctx,
                    object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            val currentZoom = zoomPercentage
                            val delta = detector.scaleFactor - 1f
                            // Actualizamos el zoom lineal (0.0 a 1.0)
                            val newZoom = (currentZoom + delta).coerceIn(0f, 1f)
                            zoomPercentage = newZoom
                            cameraControl?.setLinearZoom(newZoom)
                            return true
                        }
                    }
                )

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview
                        )
                        cameraControl = camera.cameraControl
                    } catch (e: Exception) { e.printStackTrace() }
                }, ContextCompat.getMainExecutor(ctx))

                // 2. GESTOR DE TOQUES (Enfoque + Zoom)
                previewView.setOnTouchListener { view, event ->
                    // Le avisamos al detector de zoom que revise el evento
                    scaleGestureDetector.onTouchEvent(event)

                    // Si es un solo toque (y no zoom), enfocamos
                    if (event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
                        val factory = previewView.meteringPointFactory
                        val point = factory.createPoint(event.x, event.y)
                        val action = FocusMeteringAction.Builder(point).build()
                        focusPoint = Offset(event.x, event.y)
                        cameraControl?.startFocusAndMetering(action)
                    }
                    true
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // CAPA 1: Aro de enfoque
        focusPoint?.let { offset ->
            FocusRing(offset) { focusPoint = null }
        }

        // CAPA 2: Visor animado
        VisorMinimalistaOverlay(alphaVisor = alphaAnimada)

        // --- INDICADOR DE ZOOM (Solo aparece si hay zoom activo) ---
        if (zoomPercentage > 0.05f) {
            Text(
                text = "ZOOM: ${(zoomPercentage * 100).toInt()}%",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 180.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // CAPA 3: Interfaz Figma
        Column(modifier = Modifier.fillMaxSize()) {
            CameraTopBar(onBackClicked = onBackClicked)
            Spacer(modifier = Modifier.weight(1f))
            CameraBottomControls()
        }
    }
}

// (Mantené el resto de las funciones: FocusRing, VisorMinimalistaOverlay, etc., igual que antes)

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

        // Superior Izquierda
        drawArc(
            color = colorVisor,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(padding, padding + topOffset),
            size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Superior Derecha
        drawArc(
            color = colorVisor,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(sizeW - padding - arcSize, padding + topOffset),
            size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inferior Izquierda
        drawArc(
            color = colorVisor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(padding, sizeH - padding - arcSize - bottomOffset),
            size = cornerSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inferior Derecha
        drawArc(
            color = colorVisor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(sizeW - padding - arcSize, sizeH - padding - arcSize - bottomOffset),
            size = cornerSize,
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
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(end = 48.dp)
        )
    }
}

@Composable
fun CameraBottomControls() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp, start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).border(1.dp, Color.White, CircleShape).clip(CircleShape).background(Color.DarkGray))
        Box(modifier = Modifier.size(80.dp).border(4.dp, Color.White, CircleShape).padding(6.dp).clip(CircleShape).background(Color(0xFFD25450)).clickable { /* Click! */ })
        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Cached, contentDescription = null, tint = Color.White)
        }
    }
}