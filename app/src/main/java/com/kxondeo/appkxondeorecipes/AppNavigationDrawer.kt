@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentScreen: String,
    appLanguage: AppLanguage,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val isSpanish = appLanguage == AppLanguage.Spanish

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KxRed)
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "KXONDEO RECIPES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isSpanish) "Gestión Profesional de Cocina" else "Professional Kitchen Management",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Libros de Cocina (PDF)" else "Cookbooks (PDF)", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = KxRed) },
                    selected = currentScreen == "cookbooks" || currentScreen == "pdf_viewer",
                    onClick = { onNavigate("cookbooks") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Recetas y Escandallos" else "Recipes & Food Cost") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                    selected = currentScreen == "main",
                    onClick = { onNavigate("main") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Menú y Planificación" else "Menu & Planning") },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
                    selected = currentScreen == "menu_planner",
                    onClick = { onNavigate("menu_planner") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Timer de Cocina" else "Kitchen Timer") },
                    icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    selected = currentScreen == "timer",
                    onClick = { onNavigate("timer") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Cálculo de Salario" else "Salary Calculator") },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    selected = currentScreen == "salary_calculator",
                    onClick = { onNavigate("salary_calculator") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp))

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Mi Equipo" else "My Team") },
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    selected = currentScreen == "teams",
                    onClick = { onNavigate("teams") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Mi Perfil" else "My Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = currentScreen == "profile",
                    onClick = { onNavigate("profile") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(if (isSpanish) "Configuración" else "Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = currentScreen == "settings",
                    onClick = { onNavigate("settings") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = content
    )
}
