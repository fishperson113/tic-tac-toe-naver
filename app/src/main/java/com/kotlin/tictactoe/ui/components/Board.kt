package com.kotlin.tictactoe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.tictactoe.model.GameState
import com.kotlin.tictactoe.model.Player

@Composable
fun Board(state: GameState, onCell: (Int) -> Unit) {
    val size = 96.dp
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (0..2).forEach { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (0..2).forEach { c ->
                    val idx = r * 3 + c
                    val highlight = state.winningLine.contains(idx)
                    Box(
                        modifier = Modifier
                            .size(size)
                            .background(
                                color = if (highlight) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .let { m ->
                                if (!state.aiThinking && state.board[idx] == null && state.winner == null && !state.isDraw) {
                                    m.clickable { onCell(idx) }
                                } else m
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (state.board[idx]) {
                                Player.X -> "X"
                                Player.O -> "O"
                                null -> ""
                            },
                            fontSize = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

