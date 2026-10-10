@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed

@Composable
fun MenuPlannerScreen(
    foodCosts: List<FoodCost>,
    appLanguage: AppLanguage,
    onMenuClick: () -> Unit
) {
    val isSpanish = appLanguage == AppLanguage.Spanish
    var selectedDay by remember { mutableStateOf("Lunes") }
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSpanish) "Menú y Planificación" else "Menu & Planning", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ScrollableTabRow(
                selectedTabIndex = days.indexOf(selectedDay).coerceAtLeast(0),
                edgePadding = 16.dp
            ) {
                days.forEach { day ->
                    Tab(
                        selected = day == selectedDay,
                        onClick = { selectedDay = day },
                        text = { Text(day, fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (isSpanish) "Menú del Día - $selectedDay" else "Daily Menu - $selectedDay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (foodCosts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (isSpanish) "No hay recetas configuradas para el menú." else "No recipes set for the menu.",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(foodCosts) { cost ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            ListItem(
                                headlineContent = { Text(cost.title, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(if (isSpanish) "${cost.portions} porciones • Coste: ${String.format("%.2f", cost.totalRawCost)} €" else "${cost.portions} portions • Cost: ${String.format("%.2f", cost.totalRawCost)} €") },
                                leadingContent = { Icon(Icons.Default.Dining, contentDescription = null, tint = KxRed) }
                            )
                        }
                    }
                }
            }
        }
    }
}
