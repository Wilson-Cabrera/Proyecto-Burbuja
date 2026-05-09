package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavegacionLiteral(
    navController: NavController,
    letraUsuario: String,
    onProfileClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // BARRA DE NAVEGACIÓN
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(32.dp),
            // DINÁMICO: Usamos Surface (Gris oscuro en Dark, Gris claro en Light)
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 4.dp // Un toque de sombra para que flote
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ItemNavegacionLiteral(
                    icon = Icons.Default.Home,
                    label = "Inicio",
                    selected = rutaActual == "inicio",
                    onClick = { navController.navigate("inicio") }
                )

                ItemNavegacionLiteral(
                    icon = Icons.Default.Star,
                    label = "Guardados",
                    selected = rutaActual == "guardados",
                    onClick = { navController.navigate("guardados") }
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // CÍRCULO DE PERFIL
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clickable { onProfileClick() },
            shape = CircleShape,
            // DINÁMICO: Misma superficie que la barra para mantener coherencia
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = letraUsuario.uppercase(),
                    // DINÁMICO: Usamos el primario (Cyan/Violeta) para que la letra resalte
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ItemNavegacionLiteral(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    // DINÁMICO: Si está seleccionado usamos el Primario. Si no, usamos el color de texto con opacidad.
    val colorContenido = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    val modifierSeleccionado = if (selected) {
        Modifier
            .clip(CircleShape)
            // DINÁMICO: Fondo del botón activo usando el primario con mucha transparencia
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    } else {
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .then(modifierSeleccionado),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorContenido,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = colorContenido,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}