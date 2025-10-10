package com.kotlin.tictactoe.ai

import android.util.Log
import com.kotlin.tictactoe.model.Difficulty
import com.kotlin.tictactoe.model.GameLogic
import com.kotlin.tictactoe.model.Metrics
import com.kotlin.tictactoe.model.Player

data class AIMove(val index: Int, val metrics: Metrics)

object TicTacToeAI {
    private const val TAG = "TicTacToeAI"

    fun chooseMove(board: List<Player?>, ai: Player, difficulty: Difficulty): AIMove = when (difficulty) {
        Difficulty.EASY -> easyMove(board)
        Difficulty.HARD -> hardMove(board, ai)
    }

    private fun easyMove(board: List<Player?>): AIMove {
        val moves = GameLogic.availableMoves(board)
        val pick = if (moves.isNotEmpty()) moves.random() else -1
        return AIMove(pick, Metrics(positionsEvaluated = 1, thinkingTimeMs = 0))
    }

    private fun hardMove(board: List<Player?>, ai: Player): AIMove {
        var nodes = 0
        val start = System.nanoTime()

        val rootMoves = GameLogic.availableMoves(board)
        var bestScore = Int.MIN_VALUE
        var bestMove = -1

        for (m in rootMoves) {
            val newBoard = GameLogic.makeMove(board, m, ai)
            val score = -negamax(newBoard, GameLogic.nextPlayer(ai), ai, depth = 1, alpha = Int.MIN_VALUE + 1, beta = Int.MAX_VALUE - 1) { nodes++ }
            Log.d(TAG, "Root move $m -> score $score")
            if (score > bestScore) {
                bestScore = score
                bestMove = m
            }
        }

        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        return AIMove(bestMove, Metrics(positionsEvaluated = nodes, thinkingTimeMs = elapsedMs))
    }

    /**
     * Negamax (minimax variant) with alpha-beta pruning.
     * Evaluates from the perspective of the player to move, returning higher values for positions
     * that are better for the [ai] player. Terminal wins/losses are depth-adjusted to prefer
     * quicker wins and slower losses. Increments a visit counter via [onVisit] for metrics.
     */
    private fun negamax(
        board: List<Player?>,
        toMove: Player,
        ai: Player,
        depth: Int,
        alpha: Int,
        beta: Int,
        onVisit: () -> Unit
    ): Int {
        onVisit()
        GameLogic.checkWinner(board)?.let { (winner, _) ->
            return if (winner == ai) 10 - depth else depth - 10
        }
        if (GameLogic.isDraw(board)) return 0

        var a = alpha
        var best = Int.MIN_VALUE
        for (m in GameLogic.availableMoves(board)) {
            val child = GameLogic.makeMove(board, m, toMove)
            val score = -negamax(child, GameLogic.nextPlayer(toMove), ai, depth + 1, -beta, -a, onVisit)
            if (score > best) best = score
            if (best > a) a = best
            if (a >= beta) break // prune
        }
        return best
    }
}
