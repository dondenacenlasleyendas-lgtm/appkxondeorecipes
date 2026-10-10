package com.kxondeo.appkxondeorecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kxondeo.appkxondeorecipes.ui.theme.KxondeoTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KxondeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var currentScreen by remember { mutableStateOf("main") }
                    var appLanguage by remember { mutableStateOf(AppLanguage.Spanish) }

                    val foodCosts = remember {
                        mutableStateListOf(
                            FoodCost(1, "Paella de Marisco", emptyList(), 4),
                            FoodCost(2, "Gazpacho Andaluz", emptyList(), 6)
                        )
                    }

                    val folders = remember {
                        mutableStateListOf(
                            Folder(1, "Entrantes", "Starters"),
                            Folder(2, "Platos Principales", "Main Courses")
                        )
                    }

                    val cookbooks = remember { mutableStateListOf<CookbookPdf>() }
                    var selectedCookbook by remember { mutableStateOf<CookbookPdf?>(null) }
                    var cookbookForExtraction by remember { mutableStateOf<CookbookPdf?>(null) }

                    when (currentScreen) {
                        "splash" -> SplashScreen(onNext = { currentScreen = "login" })
                        "login" -> LoginScreen(onLoginSuccess = { currentScreen = "main" })
                        "main" -> FoodCostScreen(
                            foodCosts = foodCosts,
                            folders = folders,
                            appLanguage = appLanguage,
                            onBack = { currentScreen = "login" },
                            onFoodCostClick = {},
                            onAddFoodCost = {},
                            onAddFolder = { name, parentId ->
                                val newId = (folders.maxOfOrNull { it.id } ?: 0) + 1
                                folders.add(Folder(newId, name, name, parentId))
                            },
                            onDeleteFolder = { folderId ->
                                folders.removeAll { it.id == folderId }
                            },
                            onDeleteFoodCost = { costId ->
                                foodCosts.removeAll { it.id == costId }
                            },
                            onMoveFoodCost = { _, _ -> },
                            onOpenCookbooks = { currentScreen = "cookbooks" }
                        )
                        "cookbooks" -> CookbookFolderScreen(
                            cookbooks = cookbooks,
                            appLanguage = appLanguage,
                            onBack = { currentScreen = "main" },
                            onAddCookbook = { uri ->
                                val cookbook = PdfManager.savePdfToInternalStorage(context, uri)
                                if (cookbook != null) {
                                    cookbooks.add(cookbook)
                                }
                            },
                            onOpenCookbook = { cookbook ->
                                selectedCookbook = cookbook
                                currentScreen = "pdf_viewer"
                            },
                            onExtractRecipes = { cookbook ->
                                cookbookForExtraction = cookbook
                            },
                            onDeleteCookbook = { cookbookId ->
                                cookbooks.find { it.id == cookbookId }?.let { cb ->
                                    try {
                                        File(cb.localFilePath).delete()
                                    } catch (_: Exception) {}
                                    cookbooks.remove(cb)
                                }
                            }
                        )
                        "pdf_viewer" -> {
                            selectedCookbook?.let { cb ->
                                PdfViewerScreen(
                                    cookbook = cb,
                                    appLanguage = appLanguage,
                                    onBack = { currentScreen = "cookbooks" },
                                    onExtractRecipes = { cookbook ->
                                        cookbookForExtraction = cookbook
                                    }
                                )
                            } ?: LaunchedEffect(Unit) { currentScreen = "cookbooks" }
                        }
                    }

                    cookbookForExtraction?.let { cb ->
                        RecipeExtractionDialog(
                            cookbook = cb,
                            folders = folders,
                            appLanguage = appLanguage,
                            onDismiss = { cookbookForExtraction = null },
                            onConfirmImport = { extractedRecipes, targetFolderId ->
                                extractedRecipes.forEach { r ->
                                    val newCost = FoodCost(
                                        id = (100000..999999).random(),
                                        title = r.title,
                                        ingredients = r.ingredients.map { ingName ->
                                            FoodCostIngredient(ingName, 1.0, "unidad", 1.0)
                                        },
                                        portions = r.portions,
                                        folderId = targetFolderId
                                    )
                                    foodCosts.add(newCost)
                                }
                                cookbookForExtraction = null
                                currentScreen = "main"
                            }
                        )
                    }
                }
            }
        }
    }
}
