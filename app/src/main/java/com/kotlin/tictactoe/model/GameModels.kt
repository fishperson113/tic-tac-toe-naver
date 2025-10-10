package com.kotlin.tictactoe.model

enum class Player { X, O }

enum class Difficulty { EASY, HARD }

data class Scores(
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0,
    val streakCount: Int = 0,
    val streakType: ResultType? = null,
)

enum class ResultType { X_WIN, O_WIN, DRAW }

data class Metrics(
    val positionsEvaluated: Int = 0,
    val thinkingTimeMs: Long = 0L,
)

data class GameState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val winningLine: List<Int> = emptyList(),
    val isDraw: Boolean = false,
    val scores: Scores = Scores(),
    val difficulty: Difficulty = Difficulty.EASY,
    val aiThinking: Boolean = false,
    val lastMoveMetrics: Metrics = Metrics(),
)

