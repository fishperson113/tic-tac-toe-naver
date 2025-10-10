package com.kotlin.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TicTacToeApp()
            }
        }
    }
}

@Composable
fun TicTacToeApp() {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game status
        Text(
            text = when {
                winner != null -> "Winner: $winner 🎉"
                board.flatten().none { it.isEmpty() } -> "It's a Draw 🤝"
                else -> "Turn: $currentPlayer"
            },
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Board grid
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .background(Color.LightGray)
                            .clickable(enabled = board[row][col].isEmpty() && winner == null) {
                                board = board.toMutableList().also {
                                    it[row] = it[row].toMutableList().also { rowList ->
                                        rowList[col] = currentPlayer
                                    }
                                }
                                if (checkWinner(board, currentPlayer)) {
                                    winner = currentPlayer
                                } else {
                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(board[row][col], fontSize = 32.sp)
                    }
                }
            }
        }

        // Restart button
        if (winner != null || board.flatten().none { it.isEmpty() }) {
            Button(
                onClick = {
                    board = List(3) { MutableList(3) { "" } }
                    currentPlayer = "X"
                    winner = null
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Restart")
            }
        }
    }
}

fun checkWinner(board: List<List<String>>, player: String): Boolean {
    // Rows & Columns
    for (i in 0..2) {
        if ((0..2).all { board[i][it] == player }) return true
        if ((0..2).all { board[it][i] == player }) return true
    }
    // Diagonals
    if ((0..2).all { board[it][it] == player }) return true
    if ((0..2).all { board[it][2 - it] == player }) return true

    return false
}
