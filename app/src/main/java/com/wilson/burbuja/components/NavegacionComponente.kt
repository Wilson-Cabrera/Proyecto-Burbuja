package com.wilson.burbuja.components

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
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- BARRA DE NAVEGACIÓN ---
        Surface(
            modifier = Modifier
                .weight(1f) // Toma el espacio restante
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp), // Padding interno para que los botones no toquen los bordes de la cápsula
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Pequeño espacio entre los dos botones
            ) {
                // CAMBIO CLAVE: weight(1f) y fillMaxHeight() hace que ocupen exactamente la mitad cada uno
                ItemNavegacionLiteral(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    icon = Icons.Default.Home,
                    label = "Inicio",
                    selected = rutaActual == "inicio",
                    onClick = { navController.navigate("inicio") { launchSingleTop = true } }
                )

                ItemNavegacionLiteral(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    icon = Icons.Default.Star,
                    label = "Guardados",
                    selected = rutaActual == "guardados",
                    onClick = { navController.navigate("guardados") { launchSingleTop = true } }
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- CÍRCULO DE PERFIL ---
        Surface(
            modifier = Modifier
                .size(64.dp)
                .clickable { onProfileClick() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = letraUsuario.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// CAMBIO CLAVE: Agregamos el parámetro 'modifier' para poder controlarlo desde el padre
@Composable
fun ItemNavegacionLiteral(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorContenido = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    val colorFondo = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }

    Box(
        // Aplicamos el modifier (que trae el weight 1f) y le damos forma de cápsula interna
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(colorFondo)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorContenido,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = colorContenido,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}