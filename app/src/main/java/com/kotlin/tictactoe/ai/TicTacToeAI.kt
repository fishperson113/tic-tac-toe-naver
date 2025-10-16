package com.kotlin.tictactoe.ai

import com.kotlin.tictactoe.model.Difficulty
import com.kotlin.tictactoe.model.GameLogic
import com.kotlin.tictactoe.model.Metrics
import com.kotlin.tictactoe.model.Player

data class AIMove(val index: Int, val metrics: Metrics)

object TicTacToeAI {
    fun chooseMove(board: List<com.kotlin.tictactoe.model.Player?>, ai: Player, difficulty: Difficulty): AIMove = when (difficulty) {
        Difficulty.EASY -> easyMove(board)
        Difficulty.HARD -> hardMove(board, ai)
    }

    private fun easyMove(board: List<com.kotlin.tictactoe.model.Player?>): AIMove {
        val moves = GameLogic.availableMoves(board)
        val pick = moves.firstOrNull() ?: -1
        return AIMove(pick, Metrics(positionsEvaluated = 1, thinkingTimeMs = 0))
    }

    private fun hardMove(board: List<com.kotlin.tictactoe.model.Player?>, ai: Player): AIMove {
        // Simple depth-limited negamax placeholder
        var bestMove = -1
        var bestScore = Int.MIN_VALUE
        var nodes = 0
        val start = System.nanoTime()
        val rootMoves = GameLogic.availableMoves(board)
        for (m in rootMoves) {
            val newBoard = GameLogic.makeMove(board, m, ai)
            val score = -negamax(newBoard, GameLogic.nextPlayer(ai), ai, depth = 1, alpha = Int.MIN_VALUE / 2, beta = Int.MAX_VALUE / 2) { nodes++ }
            if (score > bestScore) {
                bestScore = score
                bestMove = m
            }
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        return AIMove(bestMove, Metrics(positionsEvaluated = nodes, thinkingTimeMs = elapsedMs))
    }

    private fun evaluate(board: List<com.kotlin.tictactoe.model.Player?>, ai: Player): Int {
        GameLogic.checkWinner(board)?.let { (winner, _) ->
            return when (winner) {
                ai -> 100
                else -> -100
            }
        }
        if (GameLogic.isDraw(board)) return 0
        return 0
    }

    private fun negamax(
        board: List<com.kotlin.tictactoe.model.Player?>,
        toMove: Player,
        ai: Player,
        depth: Int,
        alpha: Int,
        beta: Int,
        onVisit: () -> Unit,
    ): Int {
        onVisit()
        GameLogic.checkWinner(board)?.let { (winner, _) ->
            return if (winner == ai) 100 - depth else -100 + depth
        }
        if (GameLogic.isDraw(board) || depth >= 6) return 0

        var a = alpha
        var best = Int.MIN_VALUE / 2
        for (m in GameLogic.availableMoves(board)) {
            val child = GameLogic.makeMove(board, m, toMove)
            val score = -negamax(child, GameLogic.nextPlayer(toMove), ai, depth + 1, -beta, -a, onVisit)
            if (score > best) best = score
            if (best > a) a = best
            if (a >= beta) break
        }
        return best
    }
}

