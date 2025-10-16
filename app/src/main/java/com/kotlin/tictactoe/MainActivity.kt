package com.kotlin.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.tictactoe.ui.AiGameScreen
import com.kotlin.tictactoe.ui.OnlineGameScreen
import com.kotlin.tictactoe.ui.theme.TicTactoeTheme
import com.kotlin.tictactoe.viewmodel.AiGameViewModel
import com.kotlin.tictactoe.viewmodel.OnlineGameViewModel

class MainActivity : ComponentActivity() {
    private val aiVm: AiGameViewModel by viewModels()
    private val onlineVm: OnlineGameViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTactoeTheme {
                val mode = remember { mutableStateOf(Mode.AI) }

                Scaffold(
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(onClick = { mode.value = Mode.AI }) { Text("Single Player (AI)") }
                            Button(onClick = { mode.value = Mode.ONLINE }) { Text("Online Multiplayer") }
                        }
                    }
                ) { inner ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                    ) {
                        when (mode.value) {
                            Mode.AI -> AiGameScreen(viewModel = aiVm)
                            Mode.ONLINE -> OnlineGameScreen(viewModel = onlineVm)
                        }
                    }
                }
            }
        }
    }
}

enum class Mode { AI, ONLINE }
