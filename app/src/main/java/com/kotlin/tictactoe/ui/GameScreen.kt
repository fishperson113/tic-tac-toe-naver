package com.kotlin.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kotlin.tictactoe.model.Difficulty
import com.kotlin.tictactoe.model.GameState
import com.kotlin.tictactoe.viewmodel.GameViewModel
import com.kotlin.tictactoe.ui.components.Board
import com.kotlin.tictactoe.ui.components.GameInfo

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    GameContent(state = state, onCell = viewModel::onCellClick, onNewGame = viewModel::newGame, onDifficulty = viewModel::setDifficulty)
}

@Composable
private fun GameContent(
    state: GameState,
    onCell: (Int) -> Unit,
    onNewGame: () -> Unit,
    onDifficulty: (Difficulty) -> Unit,
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
                state.winner != null -> "Winner: ${state.winner}"
                state.isDraw -> "Draw"
                else -> "Turn: ${state.currentPlayer}"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DifficultyDropdown(state.difficulty, onDifficulty)
            Button(onClick = onNewGame) { Text("New Game") }
        }

        Board(state = state, onCell = onCell)

        GameInfo(state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultyDropdown(current: Difficulty, onChange: (Difficulty) -> Unit) {
    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = !expanded.value }) {
        OutlinedTextField(
            value = current.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Difficulty") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            Difficulty.values().forEach { d ->
                DropdownMenuItem(text = { Text(d.name) }, onClick = {
                    onChange(d)
                    expanded.value = false
                })
            }
        }
    }
}

// Board and GameInfo moved into ui/components
