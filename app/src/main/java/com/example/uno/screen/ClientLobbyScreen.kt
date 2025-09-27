package com.example.uno.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uno.viewModel.UnoViewModel

@Composable
fun ClientLobbyScreen(viewModel: UnoViewModel, onEnterGame: () -> Unit) {
    val lobby = viewModel.lobby.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lobby - Waiting for host to start:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(lobby.value) { player ->
                Text("- ${player.name}")
            }
        }

        // Optional: you can poll viewModel.state to check if game started
        Button(onClick = onEnterGame, modifier = Modifier.fillMaxWidth()) {
            Text("Enter Game")
        }
    }
}
