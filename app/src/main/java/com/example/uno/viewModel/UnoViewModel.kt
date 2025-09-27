package com.example.uno.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uno.model.*
import com.example.uno.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnoViewModel(private val repo: GameRepository = GameRepository()) : ViewModel() {
    val lobby = repo.lobby
    val state = repo.state
    val yourHand = repo.yourHand

    fun startHost(port: Int = 9999) = repo.startHost(port)
    fun stopHost() = repo.stopHost()
    fun hostStartGame() = repo.hostStartGame()

    fun connectToHost(ip: String, name: String, onJoined: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.connectToHost(ip, name = name)
            onJoined()
        }
    }

    fun disconnectClient() = viewModelScope.launch { repo.disconnectClient() }
    fun clientPlayCard(roomId: String, playerId: String, card: Card) =
        viewModelScope.launch { repo.clientPlayCard(roomId, playerId, card) }

    fun clientDrawCard(roomId: String, playerId: String) =
        viewModelScope.launch { repo.clientDrawCard(roomId, playerId) }
}
