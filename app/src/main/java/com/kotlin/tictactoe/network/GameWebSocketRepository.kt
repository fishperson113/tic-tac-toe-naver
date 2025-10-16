package com.kotlin.tictactoe.network

import android.util.Log
import com.kotlin.tictactoe.model.ConnectionStatus
import com.kotlin.tictactoe.model.MultiplayerState
import com.kotlin.tictactoe.model.PlayerRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject

class GameWebSocketRepository(
    private val scope: CoroutineScope,
    private val serverUrl: String = DEFAULT_WS_URL,
) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    private var webSocket: WebSocket? = null

    private val _state = MutableStateFlow(MultiplayerState(connectionStatus = ConnectionStatus.DISCONNECTED))
    val state: StateFlow<MultiplayerState> = _state

    fun connect() {
        if (_state.value.connectionStatus == ConnectionStatus.CONNECTED) return
        _state.value = _state.value.copy(connectionStatus = ConnectionStatus.CONNECTING)
        val request = Request.Builder()
            .url(serverUrl)
            .build()
        webSocket = client.newWebSocket(request, Listener())
    }

    fun disconnect() {
        webSocket?.close(1000, "client closing")
        webSocket = null
        _state.value = _state.value.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
    }

    fun increment(square: Int) {
        val s = _state.value
        if (s.connectionStatus != ConnectionStatus.CONNECTED || s.gameOver) return
        val msg = JSONObject()
            .put("type", "INCREMENT")
            .put("square", square)
        webSocket?.send(msg.toString())
    }

    private inner class Listener : WebSocketListener() {
        override fun onOpen(ws: WebSocket, response: Response) {
            scope.launch(Dispatchers.Main) {
                _state.value = _state.value.copy(connectionStatus = ConnectionStatus.CONNECTED)
            }
        }

        override fun onMessage(ws: WebSocket, text: String) {
            try {
                val json = JSONObject(text)
                when (json.optString("type")) {
                    "PLAYER_ASSIGNED" -> handlePlayerAssigned(json)
                    "UPDATE" -> handleUpdate(json)
                    "GAME_OVER" -> handleGameOver(json)
                    "READY" -> handleReady()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Invalid message: $text", e)
            }
        }

        override fun onMessage(ws: WebSocket, bytes: ByteString) {
            // No binary messages expected
        }

        override fun onClosing(ws: WebSocket, code: Int, reason: String) {
            ws.close(code, reason)
        }

        override fun onClosed(ws: WebSocket, code: Int, reason: String) {
            scope.launch(Dispatchers.Main) {
                _state.value = _state.value.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
            }
        }

        override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket failure", t)
            scope.launch(Dispatchers.Main) {
                _state.value = _state.value.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
            }
        }
    }

    private fun handlePlayerAssigned(json: JSONObject) {
        val playerStr = json.optString("player").uppercase()
        val player = when (playerStr) {
            "ODD" -> PlayerRole.ODD
            "EVEN" -> PlayerRole.EVEN
            else -> null
        }
        val board = json.optJSONArray("board")?.let { arr ->
            List(arr.length()) { i -> arr.optInt(i, 0) }
        } ?: List(25) { 0 }
        scope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                player = player,
                board = board,
                waitingForOpponent = true,
                gameOver = false,
                winner = null,
                winningLine = emptyList(),
                lastUpdatedSquare = null
            )
        }
    }

    private fun handleUpdate(json: JSONObject) {
        val square = json.getInt("square")
        val value = json.getInt("value")
        scope.launch(Dispatchers.Main) {
            val current = _state.value.board.toMutableList()
            if (square in current.indices) current[square] = value
            _state.value = _state.value.copy(
                board = current,
                lastUpdatedSquare = square,
                waitingForOpponent = false
            )
        }
    }

    private fun handleGameOver(json: JSONObject) {
        val winnerStr = json.optString("winner").uppercase()
        val winner = when (winnerStr) {
            "ODD" -> PlayerRole.ODD
            "EVEN" -> PlayerRole.EVEN
            else -> null
        }
        val winningLine = json.optJSONArray("winningLine")?.toListOfInt() ?: emptyList()
        scope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                gameOver = true,
                winner = winner,
                winningLine = winningLine
            )
        }
    }

    private fun handleReady() {
        scope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(waitingForOpponent = false)
        }
    }

    private fun JSONArray.toListOfInt(): List<Int> = List(length()) { i -> optInt(i, 0) }

    companion object {
        private const val TAG = "GameWSRepo"
        // Change to your server address, e.g. ws://10.0.2.2:8080/ws
        const val DEFAULT_WS_URL = "ws://10.0.2.2:8080/ws"
    }
}
