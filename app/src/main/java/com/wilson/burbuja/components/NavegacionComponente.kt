package com.wilson.burbuja

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
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
    onProfileClick: () -> Unit,
    onGalleryClick: () -> Unit // <--- Recibimos la acción desde el Main
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
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF6E88A6).copy(alpha = 0.9f),
            shadowElevation = 0.dp
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
                    icon = Icons.Default.PhotoLibrary,
                    label = "Galeria",
                    selected = rutaActual == "galeria",
                    onClick = { onGalleryClick() } // <--- Ahora dispara el selector
                )

                ItemNavegacionLiteral(
                    icon = Icons.Default.Star,
                    label = "Guardados",
                    selected = rutaActual == "guardados",
                    onClick = { navController.navigate("guardados") }
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // LA ELIPSE CON LA LETRA
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clickable { onProfileClick() },
            shape = CircleShape,
            color = Color(0xFF6E88A6),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = letraUsuario.uppercase(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ItemNavegacionLiteral(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val colorContenido = if (selected) Color(0xFF7ACAFF) else Color.White
    val modifierSeleccionado = if (selected) {
        Modifier.clip(CircleShape).background(Color(0xFF7ACAFF).copy(alpha = 0.15f)).padding(horizontal = 16.dp, vertical = 13.dp)
    } else {
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    }
    Box(modifier = Modifier.clip(CircleShape).clickable { onClick() }.then(modifierSeleccionado), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = colorContenido, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = colorContenido, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
        }
    }
}