package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Colores del sistema de diseño
val SurfaceDark = Color(0xFF2D3748).copy(alpha = 0.9f)
val AccentCyan = Color(0xFF38BDF8)
val TextWhite = Color(0xFFFFFFFF)

@Composable
fun NavegacionLiteral(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .fillMaxWidth()
            .height(56.dp), // Altura fina de 56dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- CÁPSULA PRINCIPAL ---
        Surface(
            modifier = Modifier.weight(1f),
            shape = CircleShape,
            color = Color(0xFF6E88A6),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemNavegacion(
                    icon = Icons.Default.Home,
                    label = "Inicio",
                    isSelected = currentRoute == "inicio",
                    onClick = { if (currentRoute != "inicio") navController.navigate("inicio") }
                )
                ItemNavegacion(
                    icon = Icons.Default.PhotoLibrary,
                    label = "Galeria",
                    isSelected = currentRoute == "galeria",
                    onClick = { if (currentRoute != "galeria") navController.navigate("galeria") }
                )
                ItemNavegacion(
                    icon = Icons.Default.Star,
                    label = "Guardados",
                    isSelected = currentRoute == "guardados",
                    onClick = { if (currentRoute != "guardados") navController.navigate("guardados") }
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- CÍRCULO USUARIO ---
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = SurfaceDark,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "W", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RowScope.ItemNavegacion(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Definimos los colores para el contraste
    val colorActivoContenido = Color(0xFF1F2A37) // Oscuro para que resalte sobre el celeste
    val colorInactivoContenido = Color.White

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Eliminamos el "ripple" gris para que sea más limpio
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // --- LA ELIPSE DE SELECCIÓN ---
        if (isSelected) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(45.dp),
                shape = CircleShape,
                // Cambiá esta línea:
                color = Color(0xFF7ACAFF).copy(alpha = 0.4f) // 0.4 es 40% de opacidad
            ) {}
        }

        // --- ICONO Y TEXTO ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                // Si está seleccionado, usamos el color oscuro; si no, blanco
                tint = if (isSelected) colorActivoContenido else colorInactivoContenido,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) colorActivoContenido else colorInactivoContenido,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}