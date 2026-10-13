@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed

val DarkBg = Color(0xFF0A0E1A)
val CardDarkBg = Color(0xFF13192B)
val TextMuted = Color(0xFF8C9BAE)

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentScreen: String,
    appLanguage: AppLanguage,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkBg,
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Drawer Header matching screenshot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MENU",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = KxRed
                        )
                        IconButton(onClick = onCloseDrawer) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    // Navigation Items
                    DrawerMenuItem(
                        label = "Recetas",
                        icon = Icons.Default.Restaurant,
                        isSelected = currentScreen == "main",
                        onClick = { onNavigate("main") }
                    )

                    DrawerMenuItem(
                        label = "Menú",
                        icon = Icons.Default.CalendarMonth,
                        isSelected = currentScreen == "menu_planner",
                        onClick = { onNavigate("menu_planner") }
                    )

                    DrawerMenuItem(
                        label = "Timer",
                        icon = Icons.Default.Timer,
                        isSelected = currentScreen == "timer",
                        onClick = { onNavigate("timer") }
                    )

                    DrawerMenuItem(
                        label = "Cálculo Salario",
                        icon = Icons.Default.Payments,
                        isSelected = currentScreen == "salary_calculator",
                        onClick = { onNavigate("salary_calculator") }
                    )

                    DrawerMenuItem(
                        label = "Libros de Cocina (PDF)",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        isSelected = currentScreen == "cookbooks" || currentScreen == "pdf_viewer",
                        onClick = { onNavigate("cookbooks") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "GESTIÓN ADMIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                    )

                    DrawerMenuItem(
                        label = "Equipos",
                        icon = Icons.Default.Group,
                        isSelected = currentScreen == "teams",
                        onClick = { onNavigate("teams") }
                    )

                    DrawerMenuItem(
                        label = "Escandallos / Food Cost",
                        icon = Icons.Default.Sell,
                        isSelected = currentScreen == "food_cost",
                        onClick = { onNavigate("main") }
                    )

                    DrawerMenuItem(
                        label = "Coste Personal",
                        icon = Icons.Default.Work,
                        isSelected = currentScreen == "coste_personal",
                        onClick = { onNavigate("salary_calculator") }
                    )

                    DrawerMenuItem(
                        label = "Eventos",
                        icon = Icons.Default.Event,
                        isSelected = currentScreen == "eventos",
                        onClick = { onNavigate("menu_planner") }
                    )

                    DrawerMenuItem(
                        label = "Cuadrantes",
                        icon = Icons.Default.GridView,
                        isSelected = currentScreen == "cuadrantes",
                        onClick = { onNavigate("settings") }
                    )

                    DrawerMenuItem(
                        label = "Inventario",
                        icon = Icons.Default.Inventory2,
                        isSelected = currentScreen == "inventario",
                        onClick = { onNavigate("settings") }
                    )
                }
            }
        },
        content = content
    )
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) KxRed else Color.White

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
