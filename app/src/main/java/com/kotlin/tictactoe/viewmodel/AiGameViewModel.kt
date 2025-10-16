package com.kotlin.tictactoe.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kotlin.tictactoe.model.*
import com.kotlin.tictactoe.ai.TicTacToeAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore(name = "scores")

class AiGameViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val keyX = intPreferencesKey("xWins")
    private val keyO = intPreferencesKey("oWins")
    private val keyD = intPreferencesKey("draws")
    private val keyStreakCount = intPreferencesKey("streakCount")
    private val keyStreakType = stringPreferencesKey("streakType")

    private val dataStore by lazy { getApplication<Application>().dataStore }

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val type = prefs[keyStreakType]?.let { runCatching { ResultType.valueOf(it) }.getOrNull() }
            _state.value = _state.value.copy(
                scores = Scores(
                    xWins = prefs[keyX] ?: 0,
                    oWins = prefs[keyO] ?: 0,
                    draws = prefs[keyD] ?: 0,
                    streakCount = prefs[keyStreakCount] ?: 0,
                    streakType = type
                )
            )
        }
    }

    fun onCellClick(index: Int) {
        val s = _state.value
        if (s.winner != null || s.isDraw || s.board[index] != null || s.aiThinking) return

        val board = GameLogic.makeMove(s.board, index, s.currentPlayer)
        updateAfterMove(board, s.currentPlayer)
    }

    private fun updateAfterMove(board: List<Player?>, playerJustMoved: Player) {
        val win = GameLogic.checkWinner(board)
        if (win != null) {
            val (winner, line) = win
            val newScores = applyResult(winner)
            _state.value = _state.value.copy(
                board = board, winner = winner, winningLine = line, isDraw = false, scores = newScores
            )
            return
        }
        if (GameLogic.isDraw(board)) {
            val newScores = applyResult(null)
            _state.value = _state.value.copy(board = board, winner = null, winningLine = emptyList(), isDraw = true, scores = newScores)
            return
        }

        val next = GameLogic.nextPlayer(playerJustMoved)
        _state.value = _state.value.copy(board = board, currentPlayer = next)

        if (next == Player.O) makeAIMove()
    }

    fun newGame() {
        _state.value = _state.value.copy(
            board = List(9) { null },
            currentPlayer = Player.X,
            winner = null,
            winningLine = emptyList(),
            isDraw = false,
            lastMoveMetrics = Metrics(),
        )
    }

    fun setDifficulty(d: Difficulty) {
        _state.value = _state.value.copy(difficulty = d)
    }

    private fun applyResult(winner: Player?): Scores {
        val prev = _state.value.scores
        val resultType = when (winner) {
            Player.X -> ResultType.X_WIN
            Player.O -> ResultType.O_WIN
            null -> ResultType.DRAW
        }
        val updated = when (resultType) {
            ResultType.X_WIN -> prev.copy(xWins = prev.xWins + 1)
            ResultType.O_WIN -> prev.copy(oWins = prev.oWins + 1)
            ResultType.DRAW -> prev.copy(draws = prev.draws + 1)
        }
        val newStreakCount = if (prev.streakType == resultType) prev.streakCount + 1 else 1
        val finalScores = updated.copy(streakCount = newStreakCount, streakType = resultType)
        persistScores(finalScores)
        return finalScores
    }

    private fun persistScores(scores: Scores) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[keyX] = scores.xWins
                prefs[keyO] = scores.oWins
                prefs[keyD] = scores.draws
                prefs[keyStreakCount] = scores.streakCount
                prefs[keyStreakType] = scores.streakType?.name ?: ""
            }
        }
    }

    private fun makeAIMove() {
        val s = _state.value
        if (s.winner != null || s.isDraw) return
        _state.value = s.copy(aiThinking = true)

        viewModelScope.launch {
            val before = System.nanoTime()
            val (idx, metrics) = withContext(Dispatchers.Default) {
                val move = TicTacToeAI.chooseMove(_state.value.board, Player.O, _state.value.difficulty)
                move.index to move.metrics.copy(thinkingTimeMs = (System.nanoTime() - before) / 1_000_000)
            }
            val st = _state.value.copy(aiThinking = false, lastMoveMetrics = metrics)
            _state.value = st
            if (idx >= 0) {
                val board = GameLogic.makeMove(st.board, idx, Player.O)
                updateAfterMove(board, Player.O)
            }
        }
    }
}

