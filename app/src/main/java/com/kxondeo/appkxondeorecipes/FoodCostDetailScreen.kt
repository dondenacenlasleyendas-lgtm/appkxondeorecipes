@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed
import java.util.Locale

@Composable
fun FoodCostDetailScreen(
    costToEdit: FoodCost?,
    appLanguage: AppLanguage,
    onBack: () -> Unit,
    onSave: (FoodCost) -> Unit
) {
    var title by remember { mutableStateOf(costToEdit?.title ?: "") }
    var portions by remember { mutableStateOf(costToEdit?.portions?.toString() ?: "1") }
    var overhead by remember { mutableStateOf(costToEdit?.overheadPercentage?.toString() ?: "10") }
    var targetMargin by remember { mutableStateOf(costToEdit?.targetMarginPercentage?.toString() ?: "70") }
    
    val ingredients = remember { 
        mutableStateListOf<FoodCostIngredient>().apply { 
            if (costToEdit != null) addAll(costToEdit.ingredients) 
        } 
    }

    val currentCost = FoodCost(
        id = costToEdit?.id ?: (0..1000000).random(),
        title = title,
        ingredients = ingredients.toList(),
        portions = portions.toIntOrNull() ?: 1,
        overheadPercentage = overhead.toDoubleOrNull() ?: 10.0,
        targetMarginPercentage = targetMargin.toDoubleOrNull() ?: 70.0,
        folderId = costToEdit?.folderId
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(appLanguage, "Food Cost Template", "Plantilla de Escandallo", "Mẫu chi phí", "Matkostnad mal")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { if (title.isNotBlank()) onSave(currentCost) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(t(appLanguage, "Save", "Guardar", "Lưu", "Lagre"))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(t(appLanguage, "Recipe Name", "Nombre de la Receta", "Tên công thức", "Oppskriftsnavn")) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            itemsIndexed(ingredients) { index, item ->
                IngredientRow(
                    ingredient = item,
                    onUpdate = { ingredients[index] = it },
                    onDelete = { ingredients.removeAt(index) }
                )
            }
        }
    }
}

@Composable
fun IngredientRow(
    ingredient: FoodCostIngredient,
    onUpdate: (FoodCostIngredient) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = { onUpdate(ingredient.copy(name = it)) },
            modifier = Modifier.weight(2f)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "Delete")
        }
    }
}
