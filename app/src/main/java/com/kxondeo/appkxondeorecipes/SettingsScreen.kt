package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    isSpanish: Boolean,
    onLanguageToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isSpanish) "Configuración" else "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ListItem(
            headlineContent = { Text(if (isSpanish) "Modo Oscuro" else "Dark Mode") },
            leadingContent = { Icon(Icons.Default.LightMode, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeToggle
                )
            }
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text(if (isSpanish) "Idioma" else "Language") },
            leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
            supportingContent = { Text(if (isSpanish) "Español" else "English") },
            trailingContent = {
                TextButton(onClick = { onLanguageToggle(!isSpanish) }) {
                    Text(if (isSpanish) "Cambiar a Inglés" else "Switch to Spanish")
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Kxondeo v1.0.0",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.outline
        )
    }
}
