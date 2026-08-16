package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    isSpanish: Boolean,
    recipes: List<Recipe>,
    folders: List<Folder>,
    onBack: () -> Unit
) {
    var teamMembers by remember { mutableStateOf<List<TeamMember>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var targetUid by remember { mutableStateOf("") }
    var targetName by remember { mutableStateOf("") }
    
    var showSharePicker by remember { mutableStateOf<TeamMember?>(null) }
    var shareType by remember { mutableStateOf("RECIPE") }

    LaunchedEffect(Unit) {
        FirebaseManager.getTeams().collect { teamMembers = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSpanish) "Mi Equipo" else "My Team") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (teamMembers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (isSpanish) "No tienes miembros en tu equipo aún" else "You don't have team members yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(teamMembers) { member ->
                        ListItem(
                            headlineContent = { Text(member.name) },
                            supportingContent = { Text("ID: ${member.uid}") },
                            leadingContent = { Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp)) },
                            trailingContent = {
                                Button(onClick = { showSharePicker = member }) {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isSpanish) "Enviar" else "Send")
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
