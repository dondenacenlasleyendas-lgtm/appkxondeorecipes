@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

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
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed

@Composable
fun FoodCostDetailScreen(
    costToEdit: FoodCost?,
    appLanguage: AppLanguage,
    onBack: () -> Unit,
    onSave: (FoodCost) -> Unit,
    onDelete: ((Int) -> Unit)? = null
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

    val isSpanish = appLanguage == AppLanguage.Spanish

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (costToEdit == null)
                            (if (isSpanish) "Nueva Receta / Escandallo" else "New Recipe / Food Cost")
                        else
                            (if (isSpanish) "Editar Receta / Escandallo" else "Edit Recipe / Food Cost"),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (costToEdit != null && onDelete != null) {
                        IconButton(onClick = { onDelete(costToEdit.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Button(
                        onClick = {
                            val trimmedTitle = title.trim()
                            if (trimmedTitle.isNotBlank()) {
                                val portionsInt = portions.toIntOrNull() ?: 1
                                val overheadDouble = overhead.toDoubleOrNull() ?: 10.0
                                val marginDouble = targetMargin.toDoubleOrNull() ?: 70.0

                                val updatedCost = FoodCost(
                                    id = costToEdit?.id ?: (100000..999999).random(),
                                    title = trimmedTitle,
                                    ingredients = ingredients.toList(),
                                    portions = portionsInt,
                                    overheadPercentage = overheadDouble,
                                    targetMarginPercentage = marginDouble,
                                    folderId = costToEdit?.folderId
                                )
                                onSave(updatedCost)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KxRed)
                    ) {
                        Text(if (isSpanish) "Guardar" else "Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isSpanish) "Nombre de la Receta *" else "Recipe Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = title.isBlank()
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = portions,
                        onValueChange = { portions = it },
                        label = { Text(if (isSpanish) "Porciones / Raciones" else "Portions / Servings") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = overhead,
                        onValueChange = { overhead = it },
                        label = { Text(if (isSpanish) "% Gastos Generales" else "% Overhead") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = if (isSpanish) "Ingredientes de la Receta" else "Recipe Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(ingredients) { index, item ->
                IngredientRow(
                    ingredient = item,
                    isSpanish = isSpanish,
                    onUpdate = { ingredients[index] = it },
                    onDelete = { ingredients.removeAt(index) }
                )
            }

            item {
                Button(
                    onClick = {
                        ingredients.add(
                            FoodCostIngredient(
                                name = "",
                                quantity = 1.0,
                                unit = "kg",
                                pricePerUnit = 0.0
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isSpanish) "Añadir Ingrediente" else "Add Ingredient")
                }
            }

            if (costToEdit != null && onDelete != null) {
                item {
                    OutlinedButton(
                        onClick = { onDelete(costToEdit.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSpanish) "Eliminar esta Receta" else "Delete this Recipe")
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientRow(
    ingredient: FoodCostIngredient,
    isSpanish: Boolean,
    onUpdate: (FoodCostIngredient) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = ingredient.name,
                    onValueChange = { onUpdate(ingredient.copy(name = it)) },
                    label = { Text(if (isSpanish) "Nombre Ingrediente" else "Ingredient Name") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            var quantityStr by remember(ingredient) { mutableStateOf(if (ingredient.quantity == 0.0) "" else ingredient.quantity.toString()) }
            var priceStr by remember(ingredient) { mutableStateOf(if (ingredient.pricePerUnit == 0.0) "" else ingredient.pricePerUnit.toString()) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = {
                        quantityStr = it
                        val parsed = it.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onUpdate(ingredient.copy(quantity = parsed))
                    },
                    label = { Text(if (isSpanish) "Cantidad" else "Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = ingredient.unit,
                    onValueChange = { onUpdate(ingredient.copy(unit = it)) },
                    label = { Text(if (isSpanish) "Unidad (kg, g, l)" else "Unit") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = {
                        priceStr = it
                        val parsed = it.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onUpdate(ingredient.copy(pricePerUnit = parsed))
                    },
                    label = { Text(if (isSpanish) "Precio/Unid (€)" else "Price/Unit (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
