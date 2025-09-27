package com.example.uno.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uno.model.Card
import com.example.uno.viewModel.UnoViewModel

@Composable
fun ClientGameScreen(viewModel: UnoViewModel) {
    val state by viewModel.state.collectAsState()
    val yourHand by viewModel.yourHand.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Top Card: ${state?.topCard?.color} ${state?.topCard?.value}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Your Hand:", style = MaterialTheme.typography.titleMedium)
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(yourHand) { card ->
                CardItem(card) {
                    // TODO: call viewModel.clientPlayCard(...)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // TODO: call viewModel.clientDrawCard(...)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Draw Card")
        }
    }
}

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .size(width = 60.dp, height = 90.dp)
            .clickable { onClick() },
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("${card.color}", style = MaterialTheme.typography.bodyMedium)
            Text("${card.value}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
