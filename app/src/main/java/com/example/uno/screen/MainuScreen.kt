package com.example.uno.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onHost: () -> Unit,
    onJoin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onHost, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text("Host Game")
        }
        Button(onClick = onJoin, modifier = Modifier.fillMaxWidth()) {
            Text("Join Game")
        }
    }
}
