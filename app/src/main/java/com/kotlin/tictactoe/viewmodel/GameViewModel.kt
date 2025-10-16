package com.kotlin.tictactoe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.tictactoe.model.ConnectionStatus
import com.kotlin.tictactoe.model.MultiplayerState
import com.kotlin.tictactoe.network.GameWebSocketRepository
import kotlinx.coroutines.flow.StateFlow

class OnlineGameViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = GameWebSocketRepository(viewModelScope)

    val state: StateFlow<MultiplayerState> = repo.state

    init {
        repo.connect()
    }

    fun onCellClick(index: Int) {
        val s = state.value
        if (s.connectionStatus != ConnectionStatus.CONNECTED || s.gameOver || s.waitingForOpponent) return
        repo.increment(index)
    }

    fun reconnect() = repo.connect()
}
