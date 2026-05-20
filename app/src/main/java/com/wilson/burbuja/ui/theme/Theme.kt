package com.wilson.burbuja.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- NUEVO: Herramientas globales para cambiar el tema ---
// Estas variables viajan por toda la app para que cualquier botón pueda leer y cambiar el tema
val LocalThemeToggle = compositionLocalOf { {} } // Función vacía por defecto
val LocalThemeState = compositionLocalOf { true } // Boolean por defecto (true = oscuro)

// 1. El traje oscuro
private val BurbujaDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentViolet,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = BackgroundDark,
    onBackground = TextWhite,
    onSurface = TextWhite
)

// 2. El traje claro
private val BurbujaLightColorScheme = lightColorScheme(
    primary = AccentViolet, // Invertimos el primario para que el violeta resalte en el fondo claro
    secondary = AccentCyan,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun BurbujaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 3. Elegimos el esquema de color según el tema
    val colorScheme = if (darkTheme) BurbujaDarkColorScheme else BurbujaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // La barra superior (donde está la hora y batería) toma el color dinámico
            window.statusBarColor = colorScheme.background.toArgb()
            // Si el fondo es claro, ponemos los íconos oscuros para que se lean, y viceversa
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegura que toma tus fuentes de Type.kt
        content = content
    )
}