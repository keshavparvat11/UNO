package com.example.uno.model


import kotlinx.serialization.Serializable

@Serializable
data class PublicPlayerState(
    val id: String,
    val name: String,
    val handSize: Int
)

@Serializable
data class GameStateDTO(
    val roomId: String,
    val players: List<PublicPlayerState>,
    val topCard: Card,
    val currentPlayerId: String,
    val direction: Int,
    val deckSize: Int,
    val winnerId: String? = null
)
