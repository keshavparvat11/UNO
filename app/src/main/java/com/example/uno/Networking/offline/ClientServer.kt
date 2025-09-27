package com.example.uno.Networking.offline

import com.example.uno.model.RawMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.*
import java.net.Socket

class ClientSocket {
    private var socket: Socket? = null
    private val incomingChannel = Channel<RawMessage>(Channel.UNLIMITED)
    val incoming: Flow<RawMessage> = incomingChannel.receiveAsFlow()
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    suspend fun connect(ip: String, port: Int) {
        socket = Socket(ip, port)
        listen()
    }

    private fun listen() {
        ioScope.launch {
            val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            while (true) {
                val line = reader.readLine() ?: break
                val msg = json.decodeFromString(RawMessage.serializer(), line)
                incomingChannel.send(msg)
            }
        }
    }

    suspend fun send(msg: RawMessage) {
        ioScope.launch {
            try {
                socket?.getOutputStream()?.write((json.encodeToString(RawMessage.serializer(), msg) + "\n").toByteArray())
                socket?.getOutputStream()?.flush()
            } catch (_: Exception) {}
        }
    }

    suspend fun disconnect() {
        socket?.close()
        socket = null
    }
}
