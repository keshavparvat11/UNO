package com.example.uno.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val name: String,
    val hand: MutableList<Card> = mutableListOf()
)