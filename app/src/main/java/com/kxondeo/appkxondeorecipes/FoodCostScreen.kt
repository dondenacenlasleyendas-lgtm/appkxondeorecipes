@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onMenuClick: () -> Unit,
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
                    IconButton(onClick = { if (currentFolderId == null) onMenuClick() else currentFolderId = currentFolder?.parentFolderId }) {
                        Icon(
                            if (currentFolderId == null) Icons.Default.Menu else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenCookbooks?.invoke() }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Libros de Cocina", tint = KxRed)
                    }
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onOpenCookbooks() },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = KxRed,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isSpanish) "Libros de Cocina (PDF)" else "Cookbooks (PDF)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isSpanish) "Cargar y previsualizar PDFs o extraer recetas" else "Upload & preview PDFs or extract recipes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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
