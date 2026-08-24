package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    isSpanish: Boolean,
    onLanguageToggle: (Boolean) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(R.string.legal_privacy_policy_url)

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

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_legal_section),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.legal_privacy_policy_title)) },
            leadingContent = { Icon(Icons.Default.Policy, contentDescription = null) },
            modifier = Modifier.clickable { uriHandler.openUri(privacyPolicyUrl) }
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.legal_company_name)) },
            leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
            supportingContent = { Text("Norway Registration") }
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
