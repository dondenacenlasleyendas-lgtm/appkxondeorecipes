package com.kxondeo.appkxondeorecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kxondeo.appkxondeorecipes.ui.theme.KxondeoTheme
import kotlinx.coroutines.launch
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
                    val scope = rememberCoroutineScope()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    var currentScreen by remember { mutableStateOf("main") }
                    var appLanguage by remember { mutableStateOf(AppLanguage.Spanish) }
                    var isDarkMode by remember { mutableStateOf(false) }

                    val foodCosts = remember {
                        mutableStateListOf(
                            FoodCost(1, "Paella de Marisco", emptyList(), 4),
                            FoodCost(2, "Gazpacho Andaluz", emptyList(), 6)
                        )
                    }

                    // Folders matching user's screenshot
                    val folders = remember {
                        mutableStateListOf(
                            Folder(1, "Carne", "Meat"),
                            Folder(2, "Ensaladas", "Salads"),
                            Folder(3, "Kxondeo recipes", "Kxondeo recipes"),
                            Folder(4, "Pescado", "Fish"),
                            Folder(5, "Postres", "Desserts")
                        )
                    }

                    val cookbooks = remember { mutableStateListOf<CookbookPdf>() }
                    var selectedCookbook by remember { mutableStateOf<CookbookPdf?>(null) }
                    var cookbookForExtraction by remember { mutableStateOf<CookbookPdf?>(null) }
                    var selectedCost by remember { mutableStateOf<FoodCost?>(null) }

                    val onOpenDrawer: () -> Unit = {
                        scope.launch { drawerState.open() }
                    }

                    val onCloseDrawer: () -> Unit = {
                        scope.launch { drawerState.close() }
                    }

                    val onNavigateDrawer: (String) -> Unit = { screen ->
                        currentScreen = screen
                        scope.launch { drawerState.close() }
                    }

                    AppNavigationDrawer(
                        drawerState = drawerState,
                        currentScreen = currentScreen,
                        appLanguage = appLanguage,
                        onNavigate = onNavigateDrawer,
                        onCloseDrawer = onCloseDrawer
                    ) {
                        when (currentScreen) {
                            "splash" -> SplashScreen(onNext = { currentScreen = "login" })
                            "login" -> LoginScreen(onLoginSuccess = { currentScreen = "main" })
                            "main" -> FoodCostScreen(
                                foodCosts = foodCosts,
                                folders = folders,
                                appLanguage = appLanguage,
                                onMenuClick = onOpenDrawer,
                                onBack = { currentScreen = "login" },
                                onFoodCostClick = { selectedCost = it; currentScreen = "cost_detail" },
                                onAddFoodCost = { selectedCost = null; currentScreen = "cost_detail" },
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
                                onOpenCookbooks = { currentScreen = "cookbooks" },
                                onNavigateTab = { tabScreen -> currentScreen = tabScreen },
                                onClearAllRecipes = { foodCosts.clear() }
                            )
                            "cost_detail" -> FoodCostDetailScreen(
                                costToEdit = selectedCost,
                                appLanguage = appLanguage,
                                onBack = { selectedCost = null; currentScreen = "main" },
                                onSave = { updated ->
                                    val index = foodCosts.indexOfFirst { it.id == updated.id }
                                    if (index >= 0) {
                                        foodCosts[index] = updated
                                    } else {
                                        foodCosts.add(updated)
                                    }
                                    selectedCost = null
                                    currentScreen = "main"
                                },
                                onDelete = { costId ->
                                    foodCosts.removeAll { it.id == costId }
                                    selectedCost = null
                                    currentScreen = "main"
                                }
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
                            "menu_planner" -> MenuPlannerScreen(
                                foodCosts = foodCosts,
                                appLanguage = appLanguage,
                                onMenuClick = onOpenDrawer
                            )
                            "timer" -> TimerScreen(
                                appLanguage = appLanguage,
                                onMenuClick = onOpenDrawer
                            )
                            "salary_calculator" -> SalaryCalculatorScreen(
                                appLanguage = appLanguage,
                                onMenuClick = onOpenDrawer
                            )
                            "teams" -> TeamsScreen(
                                isSpanish = appLanguage == AppLanguage.Spanish,
                                recipes = emptyList(),
                                folders = folders,
                                onBack = { currentScreen = "main" }
                            )
                            "profile" -> ProfileScreen(
                                userId = "USER-KX9821",
                                appLanguage = appLanguage,
                                onBack = { currentScreen = "main" }
                            )
                            "settings" -> SettingsScreen(
                                isDarkMode = isDarkMode,
                                onDarkModeToggle = { isDarkMode = it },
                                isSpanish = appLanguage == AppLanguage.Spanish,
                                onLanguageToggle = { isSpanish ->
                                    appLanguage = if (isSpanish) AppLanguage.Spanish else AppLanguage.English
                                }
                            )
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
