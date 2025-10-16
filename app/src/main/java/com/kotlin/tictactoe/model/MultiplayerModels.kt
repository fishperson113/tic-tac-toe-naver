package com.kotlin.tictactoe.model

// Multiplayer 5x5 Odd/Even game models

enum class PlayerRole { ODD, EVEN }

enum class ConnectionStatus { DISCONNECTED, CONNECTING, CONNECTED }

data class MultiplayerState(
    val board: List<Int> = List(25) { 0 },
    val player: PlayerRole? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val waitingForOpponent: Boolean = true,
    val gameOver: Boolean = false,
    val winner: PlayerRole? = null,
    val winningLine: List<Int> = emptyList(),
    val lastUpdatedSquare: Int? = null,
)

