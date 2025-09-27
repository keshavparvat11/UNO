package com.example.uno.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RawMessage(val action: String, val data: JsonElement? = null)
