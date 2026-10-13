@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed

@Composable
fun FoodCostScreen(
    foodCosts: List<FoodCost>,
    folders: List<Folder>,
    appLanguage: AppLanguage,
    onMenuClick: () -> Unit,
    onBack: () -> Unit,
    onFoodCostClick: (FoodCost) -> Unit,
    onAddFoodCost: () -> Unit,
    onAddFolder: (String, Int?) -> Unit,
    onDeleteFolder: (Int) -> Unit,
    onDeleteFoodCost: (Int) -> Unit,
    onMoveFoodCost: (Int, Int?) -> Unit,
    onOpenCookbooks: (() -> Unit)? = null,
    onNavigateTab: ((String) -> Unit)? = null,
    onClearAllRecipes: (() -> Unit)? = null
) {
    var currentFolderId by remember { mutableStateOf<Int?>(null) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var folderToDelete by remember { mutableStateOf<Folder?>(null) }
    var costToDelete by remember { mutableStateOf<FoodCost?>(null) }
    var newFolderName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val isSpanish = appLanguage == AppLanguage.Spanish
    val currentFolder = folders.find { it.id == currentFolderId }
    val subfolders = folders.filter {
        it.parentFolderId == currentFolderId && (searchQuery.isBlank() || it.getName(isSpanish).contains(searchQuery, ignoreCase = true))
    }
    val costsInFolder = foodCosts.filter {
        it.folderId == currentFolderId && (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = {
                    Text(
                        text = currentFolder?.getName(isSpanish) ?: "KXONDEO",
                        color = KxRed,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentFolderId == null) onMenuClick() else currentFolderId = currentFolder?.parentFolderId
                    }) {
                        Icon(
                            if (currentFolderId == null) Icons.Default.Menu else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (foodCosts.isNotEmpty() && onClearAllRecipes != null) {
                        IconButton(onClick = { showClearAllDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Vaciar Recetas", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = { onOpenCookbooks?.invoke() }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Libros PDF", tint = KxRed)
                    }
                    IconButton(onClick = { showAddFolderDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "Nueva Carpeta", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentTab = "recipes",
                onTabSelect = { tab ->
                    when (tab) {
                        "recipes" -> { currentFolderId = null }
                        "menu" -> onNavigateTab?.invoke("menu_planner")
                        "timer" -> onNavigateTab?.invoke("timer")
                        "salary" -> onNavigateTab?.invoke("salary_calculator")
                    }
                },
                onAddClick = onAddFoodCost
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Title & Search
            Text(
                text = currentFolder?.getName(isSpanish) ?: (if (isSpanish) "Recetas" else "Recipes"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = KxRed,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isSpanish) "Buscar recetas o carpetas..." else "Search recipes or folders...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardDarkBg,
                    unfocusedContainerColor = CardDarkBg,
                    focusedBorderColor = KxRed,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Special Card for Libros de Cocina PDF
                if (currentFolderId == null && onOpenCookbooks != null && searchQuery.isBlank()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenCookbooks() },
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KxRed.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = KxRed,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isSpanish) "Libros de Cocina (PDF)" else "Cookbooks (PDF)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isSpanish) "Cargar, ver PDFs y extraer recetas" else "Upload, preview PDFs & extract recipes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KxRed)
                            }
                        }
                    }
                }

                // Default folders matching screenshot (Carne, Ensaladas, Kxondeo recipes, Pescado, Postres)
                items(subfolders) { folder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { currentFolderId = folder.id },
                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = KxRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = folder.getName(isSpanish),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { folderToDelete = folder }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                }

                // Recipe items in current folder
                items(costsInFolder) { cost ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFoodCostClick(cost) },
                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = KxRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cost.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${cost.portions} raciones • ${String.format("%.2f", cost.totalRawCost)} €",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            IconButton(onClick = { onFoodCostClick(cost) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onDeleteFoodCost(cost.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddFolderDialog) {
        AlertDialog(
            onDismissRequest = { showAddFolderDialog = false },
            title = { Text(if (isSpanish) "Nueva Carpeta" else "New Folder") },
            text = { OutlinedTextField(value = newFolderName, onValueChange = { newFolderName = it }, label = { Text(if (isSpanish) "Nombre de la Carpeta" else "Folder Name") }) },
            confirmButton = {
                TextButton(onClick = { if (newFolderName.isNotBlank()) { onAddFolder(newFolderName, currentFolderId); newFolderName = ""; showAddFolderDialog = false } }) { Text(if (isSpanish) "Crear" else "Create") }
            },
            dismissButton = { TextButton(onClick = { showAddFolderDialog = false }) { Text(if (isSpanish) "Cancelar" else "Cancel") } }
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text(if (isSpanish) "Eliminar todas las recetas" else "Delete all recipes") },
            text = { Text(if (isSpanish) "¿Estás seguro de que deseas eliminar las ${foodCosts.size} recetas registradas?" else "Are you sure you want to delete all ${foodCosts.size} recipes?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllRecipes?.invoke()
                        showClearAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (isSpanish) "Eliminar Todas" else "Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(if (isSpanish) "Cancelar" else "Cancel")
                }
            }
        )
    }

    folderToDelete?.let { folder ->
        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = { Text(if (isSpanish) "Eliminar Carpeta" else "Delete Folder") },
            text = { Text(if (isSpanish) "¿Estás seguro de que deseas eliminar '${folder.getName(isSpanish)}'?" else "Are you sure you want to delete '${folder.getName(isSpanish)}'?") },
            confirmButton = {
                Button(onClick = { onDeleteFolder(folder.id); folderToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text(if (isSpanish) "Eliminar" else "Delete")
                }
            },
            dismissButton = { TextButton(onClick = { folderToDelete = null }) { Text(if (isSpanish) "Cancelar" else "Cancel") } }
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabSelect: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = DarkBg,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Recetas", Icons.Default.Restaurant, currentTab == "recipes") { onTabSelect("recipes") }
            BottomNavItem("Menú", Icons.Default.CalendarMonth, currentTab == "menu") { onTabSelect("menu") }

            // Central FAB
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = KxRed,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

            BottomNavItem("Timer", Icons.Default.Timer, currentTab == "timer") { onTabSelect("timer") }
            BottomNavItem("Salario", Icons.Default.Payments, currentTab == "salary") { onTabSelect("salary") }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) KxRed else TextMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = color, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}
