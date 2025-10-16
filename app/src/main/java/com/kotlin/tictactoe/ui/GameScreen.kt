package com.kotlin.tictactoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kotlin.tictactoe.model.ConnectionStatus
import com.kotlin.tictactoe.model.MultiplayerState
import com.kotlin.tictactoe.viewmodel.OnlineGameViewModel
import com.kotlin.tictactoe.ui.components.NumberBoard

@Composable
fun OnlineGameScreen(viewModel: OnlineGameViewModel) {
    val state by viewModel.state.collectAsState()
    GameContent(state = state, onCell = viewModel::onCellClick, onReconnect = viewModel::reconnect)
}

@Composable
private fun GameContent(
    state: MultiplayerState,
    onCell: (Int) -> Unit,
    onReconnect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = when {
                state.gameOver && state.winner != null -> "Game Over: ${state.winner} wins"
                state.gameOver && state.winner == null -> "Game Over"
                state.waitingForOpponent -> "Waiting for opponent..."
                else -> when (state.connectionStatus) {
                    ConnectionStatus.CONNECTED -> "Connected"
                    ConnectionStatus.CONNECTING -> "Connecting..."
                    ConnectionStatus.DISCONNECTED -> "Disconnected"
                }
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(Modifier.padding(4.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("You are: ${state.player ?: "-"}")
                Text("Status: ${state.connectionStatus}")
            }
        }

        NumberBoard(
            board = state.board,
            enabled = state.connectionStatus == ConnectionStatus.CONNECTED && !state.waitingForOpponent && !state.gameOver,
            lastUpdated = state.lastUpdatedSquare,
            onCell = onCell
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.connectionStatus == ConnectionStatus.DISCONNECTED) {
                Button(onClick = onReconnect) { Text("Reconnect") }
            }
        }
    }
}
