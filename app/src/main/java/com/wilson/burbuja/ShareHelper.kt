package com.wilson.burbuja

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    fun shareStoryCard(context: Context, storyData: StoryData) {
        val activity = context as android.app.Activity
        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)

        CoroutineScope(Dispatchers.Main).launch {
            // 1. Descargamos la foto (Lógica intacta y funcional)
            var bgBitmap: Bitmap? = null
            if (storyData.photoUri.isNotEmpty()) {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(storyData.photoUri)
                    .allowHardware(false)
                    .build()

                val result = loader.execute(request)
                if (result is SuccessResult) {
                    bgBitmap = (result.drawable as? BitmapDrawable)?.bitmap
                }
            }

            // 2. Renderizamos la nueva vista minimalista
            val composeView = ComposeView(context).apply {
                alpha = 0.01f
                setContent {
                    StoryShareCardPremium(storyData, bgBitmap)
                }
            }

            rootView.addView(composeView)
            delay(150)

            composeView.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
            )
            composeView.layout(0, 0, 1080, 1920)

            val finalBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(finalBitmap)
            composeView.draw(canvas)
            rootView.removeView(composeView)

            // Guardado con nombre dinámico para evitar la caché maldita
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "burbuja_share_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Dale voz a lo cotidiano. Estoy usando Burbuja AI para descubrir el relato oculto en mi entorno.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Compartir historia con..."))
        }
    }
}

// --- EL COMPOSABLE PREMIUM REDISEÑADO ---
@Composable
private fun StoryShareCardPremium(storyData: StoryData, bgBitmap: Bitmap?) {
    val bgColor = Color(0xFF101820) // Un oscuro más profundo
    val tecnoBlue = Color(0xFF7BCBFF)
    val textColor = Color.White

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // --- LA FOTO DE FONDO ---
        if (bgBitmap != null) {
            Image(
                bitmap = bgBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().blur(40.dp), // Aún más blur para limpieza visual
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )
        }

        // --- OVERLAY TIPO VIÑETA ---
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(bgColor.copy(alpha = 0.85f), bgColor.copy(alpha = 0.3f), bgColor.copy(alpha = 0.95f)),
                    startY = 0f,
                    endY = 1920f
                )
            )
        )

        // --- DISTRIBUCIÓN FLEXBOX ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 70.dp), // Más padding lateral para encuadrar
            verticalArrangement = Arrangement.SpaceBetween, // Esto es magia: separa arriba, centro y abajo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. TOP: Etiqueta sutil tecno
            Text(
                text = "DESCUBRIMIENTO",
                color = tecnoBlue.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            // 2. MIDDLE: Título y relato (ahora con aire)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = storyData.title.ifEmpty { "Fragmentos de Realidad" }.uppercase(),
                    color = textColor,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 46.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "“${storyData.resultStory.ifEmpty { "Generando relato..." }}”",
                    color = textColor.copy(alpha = 0.9f),
                    fontSize = 24.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. BOTTOM: Branding ultra minimalista
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_simbolo),
                    contentDescription = "Burbuja AI Logo",
                    modifier = Modifier.size(36.dp), // UX PRO: Tamaño elegante, no gigante
                    colorFilter = ColorFilter.tint(tecnoBlue)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "BURBUJA AI",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                )
            }
        }
    }
}