package com.example.uno.model


import kotlinx.serialization.Serializable

@Serializable
enum class Color { RED, BLUE, GREEN, YELLOW, WILD }

@Serializable
enum class Value {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
    SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
}

@Serializable
data class Card(val color: Color, val value: Value)