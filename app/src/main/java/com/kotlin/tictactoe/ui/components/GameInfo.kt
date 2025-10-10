package com.kotlin.tictactoe.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.tictactoe.model.GameState
import com.kotlin.tictactoe.model.ResultType

@Composable
fun GameInfo(state: GameState) {
    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Scores", style = MaterialTheme.typography.titleMedium)
            Text("X Wins: ${state.scores.xWins}  |  O Wins: ${state.scores.oWins}  |  Draws: ${state.scores.draws}")
            val streakLabel = when (state.scores.streakType) {
                ResultType.X_WIN -> "X Win"
                ResultType.O_WIN -> "O Win"
                ResultType.DRAW -> "Draw"
                null -> null
            }
            Text("Streak: ${streakLabel ?: "-"} ${if (streakLabel != null) state.scores.streakCount else ""}")

            Divider()
            Text("AI Metrics (last move)", style = MaterialTheme.typography.titleSmall)
            Text("Positions evaluated: ${state.lastMoveMetrics.positionsEvaluated}")
            Text("Thinking time: ${state.lastMoveMetrics.thinkingTimeMs} ms")
            if (state.aiThinking) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

