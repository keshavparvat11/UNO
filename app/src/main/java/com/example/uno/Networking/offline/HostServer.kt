package com.example.uno.Networking.offline

import com.example.uno.model.*
import com.example.uno.repository.GameRepository
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class HostServer(private val port: Int, private val repository: GameRepository) {
    private val clients = mutableListOf<Socket>()
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private var server: ServerSocket? = null

    fun start() {
        ioScope.launch {
            server = ServerSocket(port)
            while (true) {
                val client = server!!.accept()
                clients.add(client)
                launch { handleClient(client) }
            }
        }
    }

    private suspend fun handleClient(client: Socket) {
        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
        val writer = PrintWriter(client.getOutputStream(), true)
        while (true) {
            val line = reader.readLine() ?: break
            val msg = json.decodeFromString(RawMessage.serializer(), line)
            when (msg.action) {
                "join" -> {
                    val name = msg.data!!.jsonObject["name"]!!.jsonPrimitive.content
                    val player = PublicPlayerState(
                        id = System.currentTimeMillis().toString(), name = name,
                        handSize = 0
                    )
                    repository.addPlayerToLobby(player)
                }
            }
        }
    }

    fun broadcastToClients(msg: RawMessage) {
        val text = json.encodeToString(RawMessage.serializer(), msg) + "\n"
        clients.forEach {
            try {
                it.getOutputStream().write(text.toByteArray())
                it.getOutputStream().flush()
            } catch (_: Exception) {}
        }
    }

    fun stop() {
        ioScope.launch {
            clients.forEach { it.close() }
            server?.close()
            clients.clear()
        }
    }
}
