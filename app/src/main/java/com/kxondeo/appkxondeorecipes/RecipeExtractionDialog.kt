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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RecipeExtractionDialog(
    cookbook: CookbookPdf,
    folders: List<Folder>,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmImport: (extractedRecipes: List<Recipe>, targetFolderId: Int?) -> Unit
) {
    val isSpanish = appLanguage == AppLanguage.Spanish

    var selectedFolderId by remember { mutableStateOf<Int?>(null) }
    var extractedRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val normalFolders = remember(folders) { folders.filter { !it.isCookbookFolder } }
    val selectedFolder = normalFolders.find { it.id == selectedFolderId }

    LaunchedEffect(cookbook) {
        isLoading = true
        extractedRecipes = withContext(Dispatchers.IO) {
            PdfManager.extractRecipesFromPdf(cookbook, selectedFolderId)
        }
        isLoading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KxRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSpanish) "Extraer Recetas" else "Extract Recipes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                Text(
                    text = if (isSpanish) 
                        "Libro: ${cookbook.title}" 
                    else 
                        "Book: ${cookbook.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSpanish) "Carpeta Destino:" else "Target Folder:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFolder?.getName(isSpanish) ?: if (isSpanish) "Carpetas Principales (Raíz)" else "Main Folders (Root)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isSpanish) "Carpetas Principales (Raíz)" else "Main Folders (Root)") },
                            onClick = {
                                selectedFolderId = null
                                dropdownExpanded = false
                            }
                        )
                        normalFolders.forEach { folder ->
                            DropdownMenuItem(
                                text = { Text(folder.getName(isSpanish)) },
                                onClick = {
                                    selectedFolderId = folder.id
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = KxRed)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (isSpanish) "Analizando PDF del libro..." else "Analyzing cookbook PDF...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (isSpanish) 
                            "Recetas Encontradas (${extractedRecipes.size}):" 
                        else 
                            "Found Recipes (${extractedRecipes.size}):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(extractedRecipes) { recipe ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = KxRed)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = recipe.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isSpanish) 
                                                "${recipe.ingredients.size} ingredientes • ${recipe.steps.size} pasos" 
                                            else 
                                                "${recipe.ingredients.size} ingredients • ${recipe.steps.size} steps",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedRecipes = extractedRecipes.map { it.copy(folderId = selectedFolderId) }
                    onConfirmImport(updatedRecipes, selectedFolderId)
                },
                enabled = !isLoading && extractedRecipes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = KxRed)
            ) {
                Text(
                    if (isSpanish) 
                        "Importar ${extractedRecipes.size} Recetas" 
                    else 
                        "Import ${extractedRecipes.size} Recipes"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isSpanish) "Cancelar" else "Cancel")
            }
        }
    )
}
