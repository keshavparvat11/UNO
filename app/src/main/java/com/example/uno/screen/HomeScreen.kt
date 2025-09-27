package com.example.uno.screen



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uno.component.getLocalIpAddress
import com.example.uno.viewModel.UnoViewModel

@Composable
fun HostScreen(viewModel: UnoViewModel) {
    val lobby = viewModel.lobby.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your IP: ${getLocalIpAddress() ?: "Unknown"}")
        Text("Connected Players:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(lobby.value) { player ->
                Text("- ${player.name}")
            }
        }

        Button(
            onClick = { viewModel.hostStartGame() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Game")
        }
    }
}
