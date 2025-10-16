package com.kotlin.tictactoe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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

@Composable
fun NumberBoard(
    board: List<Int>,
    enabled: Boolean,
    lastUpdated: Int?,
    onCell: (Int) -> Unit
) {
    val size = 64.dp
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        (0 until 5).forEach { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (0 until 5).forEach { c ->
                    val idx = r * 5 + c
                    val highlight = lastUpdated == idx
                    Box(
                        modifier = Modifier
                            .size(size)
                            .background(
                                color = if (highlight) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .let { m -> if (enabled) m.clickable { onCell(idx) } else m },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[idx].toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

