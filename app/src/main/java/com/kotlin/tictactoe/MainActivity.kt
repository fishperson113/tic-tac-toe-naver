package com.kotlin.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kotlin.tictactoe.ui.GameScreen
import com.kotlin.tictactoe.ui.theme.TicTactoeTheme
import com.kotlin.tictactoe.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTactoeTheme {
                GameScreen(viewModel = vm)
            }
        }
    }
}

