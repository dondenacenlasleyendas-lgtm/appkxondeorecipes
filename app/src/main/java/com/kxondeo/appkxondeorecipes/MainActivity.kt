package com.kxondeo.appkxondeorecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kxondeo.appkxondeorecipes.ui.theme.KxondeoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KxondeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("splash") }
                    
                    when (currentScreen) {
                        "splash" -> SplashScreen(onNext = { currentScreen = "login" })
                        "login" -> LoginScreen(onLoginSuccess = { currentScreen = "main" })
                        "main" -> FoodCostScreen(
                            foodCosts = listOf(
                                FoodCost(1, "Paella de Marisco", emptyList(), 4),
                                FoodCost(2, "Gazpacho Andaluz", emptyList(), 6)
                            ),
                            folders = listOf(
                                Folder(1, "Entrantes", "Starters"),
                                Folder(2, "Platos Principales", "Main Courses")
                            ),
                            appLanguage = AppLanguage.Spanish,
                            onBack = { currentScreen = "login" },
                            onFoodCostClick = {},
                            onAddFoodCost = {},
                            onAddFolder = { _, _ -> },
                            onDeleteFolder = {},
                            onDeleteFoodCost = {},
                            onMoveFoodCost = { _, _ -> }
                        )
                    }
                }
            }
        }
    }
}
