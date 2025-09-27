package com.example.uno.repository

import com.example.uno.model.*
import com.example.uno.Networking.offline.ClientSocket
import com.example.uno.Networking.offline.HostServer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

class GameRepository {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Host mode
    private var hostServer: HostServer? = null

    // Client mode
    private var clientSocket: ClientSocket? = null

    // --- Flows for Compose UI ---
    private val _state = MutableStateFlow<GameStateDTO?>(null)
    val state = _state.asStateFlow()

    private val _yourHand = MutableStateFlow<List<Card>>(emptyList())
    val yourHand = _yourHand.asStateFlow()

    private val _lobby = MutableStateFlow<List<PublicPlayerState>>(emptyList())
    val lobby = _lobby.asStateFlow()

    private var hostId = "HOST" // unique ID for host

    // ---------------- Host API ----------------
    fun startHost(port: Int = 9999) {
        hostServer = HostServer(port, this)
        hostServer?.start()
        // Add host to lobby
        _lobby.value = listOf(PublicPlayerState(hostId, "Host", 0))
    }

    fun stopHost() {
        mainScope.launch {
            hostServer?.stop()
            hostServer = null
        }
    }

    fun hostStartGame() {
        mainScope.launch {
            val players = _lobby.value
            if (players.isEmpty()) return@launch

            val deck = createDeck().shuffled().toMutableList()
            val hands = players.associate { it.id to List(7) { deck.removeAt(0) } }

            val gameState = GameStateDTO(
                roomId = "host_room",
                players = players,
                topCard = deck.removeAt(0),
                currentPlayerId = players.first().id,
                direction = 1,
                deckSize = deck.size
            )
            _state.value = gameState
            _yourHand.value = hands[hostId] ?: emptyList()

            // Broadcast game start
            hostServer?.broadcastToClients(
                RawMessage("game_start", json.encodeToJsonElement(gameState))
            )
        }
    }

    fun addPlayerToLobby(player: PublicPlayerState) {
        _lobby.value = _lobby.value + player
        hostServer?.broadcastToClients(
            RawMessage("lobby", buildJsonObject {
                put("players", json.encodeToJsonElement(_lobby.value))
            })
        )
    }

    // ---------------- Client API ----------------
    suspend fun connectToHost(ip: String, port: Int = 9999, name: String) {
        disconnectClient()
        clientSocket = ClientSocket()
        clientSocket!!.connect(ip, port)

        mainScope.launch {
            clientSocket!!.incoming.collect { raw ->
                when (raw.action) {
                    "joined" -> {}
                    "lobby" -> {
                        val arr = raw.data?.jsonObject?.get("players")?.jsonArray
                        val players = arr?.map {
                            val o = it.jsonObject
                            PublicPlayerState(
                                o["id"]!!.jsonPrimitive.content,
                                o["name"]!!.jsonPrimitive.content,
                                0
                            )
                        } ?: emptyList()
                        _lobby.value = players
                    }
                    "game_start" -> {
                        val gs = json.decodeFromJsonElement(GameStateDTO.serializer(), raw.data!!)
                        _state.value = gs
                        // TODO: assign yourHand based on your playerId
                    }
                    else -> {}
                }
            }
        }

        val payload = buildJsonObject { put("name", JsonPrimitive(name)) }
        clientSocket!!.send(RawMessage("join", payload))
    }

    suspend fun disconnectClient() {
        clientSocket?.disconnect()
        clientSocket = null
        _state.value = null
        _yourHand.value = emptyList()
    }

    suspend fun clientPlayCard(roomId: String, playerId: String, card: Card) {
        val payload = buildJsonObject {
            put("roomId", JsonPrimitive(roomId))
            put("playerId", JsonPrimitive(playerId))
            put("card", json.encodeToJsonElement(Card.serializer(), card))
        }
        clientSocket?.send(RawMessage("play_card", payload))
    }

    suspend fun clientDrawCard(roomId: String, playerId: String) {
        val payload = buildJsonObject {
            put("roomId", JsonPrimitive(roomId))
            put("playerId", JsonPrimitive(playerId))
        }
        clientSocket?.send(RawMessage("draw_card", payload))
    }

    // ---------------- Helpers ----------------
    private fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
        for (color in colors) {
            deck.add(Card(color, Value.ZERO))
            for (i in 1..9) {
                val v = Value.values()[i]
                deck.add(Card(color, v))
                deck.add(Card(color, v))
            }
            deck.add(Card(color, Value.SKIP))
            deck.add(Card(color, Value.SKIP))
            deck.add(Card(color, Value.REVERSE))
            deck.add(Card(color, Value.REVERSE))
            deck.add(Card(color, Value.DRAW_TWO))
            deck.add(Card(color, Value.DRAW_TWO))
        }
        repeat(4) {
            deck.add(Card(Color.WILD, Value.WILD))
            deck.add(Card(Color.WILD, Value.WILD_DRAW_FOUR))
        }
        return deck
    }
}
