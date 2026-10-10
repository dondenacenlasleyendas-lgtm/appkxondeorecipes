@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
fun FoodCostScreen(
    foodCosts: List<FoodCost>,
    folders: List<Folder>,
    appLanguage: AppLanguage,
    onBack: () -> Unit,
    onFoodCostClick: (FoodCost) -> Unit,
    onAddFoodCost: () -> Unit,
    onAddFolder: (String, Int?) -> Unit,
    onDeleteFolder: (Int) -> Unit,
    onDeleteFoodCost: (Int) -> Unit,
    onMoveFoodCost: (Int, Int?) -> Unit,
    onOpenCookbooks: (() -> Unit)? = null
) {
    var currentFolderId by remember { mutableStateOf<Int?>(null) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var folderToDelete by remember { mutableStateOf<Folder?>(null) }
    var costToDelete by remember { mutableStateOf<FoodCost?>(null) }
    var newFolderName by remember { mutableStateOf("") }

    val isSpanish = appLanguage == AppLanguage.Spanish
    val currentFolder = folders.find { it.id == currentFolderId }
    val subfolders = folders.filter { it.parentFolderId == currentFolderId }
    val costsInFolder = foodCosts.filter { it.folderId == currentFolderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentFolder?.getName(isSpanish) ?: t(appLanguage, "Food Cost", "Escandallos", "Chi phí thực phẩm", "Matkostnad"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (currentFolderId == null) onBack() else currentFolderId = currentFolder?.parentFolderId }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFolderDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "Add Folder")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFoodCost, containerColor = KxRed, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Add Food Cost")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (currentFolderId == null && onOpenCookbooks != null) {
                    item {
                        ListItem(
                            headlineContent = { Text(if (isSpanish) "Libros de Cocina (PDF)" else "Cookbooks (PDF)", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(if (isSpanish) "Cargar y previsualizar PDFs, extraer recetas" else "Upload & preview PDFs, extract recipes") },
                            leadingContent = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = KxRed) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { onOpenCookbooks() }
                        )
                        HorizontalDivider()
                    }
                }
                items(subfolders) { folder ->
                    ListItem(
                        headlineContent = { Text(folder.getName(isSpanish)) },
                        leadingContent = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = KxRed) },
                        trailingContent = {
                            IconButton(onClick = { folderToDelete = folder }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.clickable { currentFolderId = folder.id }
                    )
                }
                items(costsInFolder) { cost ->
                    ListItem(
                        headlineContent = { Text(cost.title) },
                        supportingContent = { Text("${String.format("%.2f", cost.totalRawCost)} €") },
                        leadingContent = { Icon(Icons.Default.Payments, contentDescription = null, tint = KxRed) },
                        trailingContent = {
                            IconButton(onClick = { costToDelete = cost }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.clickable { onFoodCostClick(cost) }
                    )
                }
            }
        }
    }

    if (showAddFolderDialog) {
        AlertDialog(
            onDismissRequest = { showAddFolderDialog = false },
            title = { Text(t(appLanguage, "New Folder", "Nueva Carpeta", "Thư mục mới", "Ny mappe")) },
            text = { OutlinedTextField(value = newFolderName, onValueChange = { newFolderName = it }, label = { Text(t(appLanguage, "Name", "Nombre", "Tên", "Navn")) }) },
            confirmButton = {
                TextButton(onClick = { if (newFolderName.isNotBlank()) { onAddFolder(newFolderName, currentFolderId); newFolderName = ""; showAddFolderDialog = false } }) { Text(t(appLanguage, "Create", "Crear", "Tạo", "Opprett")) }
            },
            dismissButton = { TextButton(onClick = { showAddFolderDialog = false }) { Text(t(appLanguage, "Cancel", "Cancelar", "Hủy", "Avbryt")) } }
        )
    }
}
