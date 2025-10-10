package com.kotlin.tictactoe.model

object GameLogic {
    val winningLines: List<List<Int>> = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
        listOf(0, 4, 8), listOf(2, 4, 6) // diagonals
    )

    fun availableMoves(board: List<Player?>): List<Int> = board.indices.filter { board[it] == null }

    fun checkWinner(board: List<Player?>): Pair<Player, List<Int>>? {
        for (line in winningLines) {
            val (a, b, c) = line
            val p = board[a]
            if (p != null && board[b] == p && board[c] == p) return p to line
        }
        return null
    }

    fun isDraw(board: List<Player?>): Boolean = checkWinner(board) == null && board.all { it != null }

    fun makeMove(board: List<Player?>, index: Int, player: Player): List<Player?> {
        if (board[index] != null) return board
        val mutable = board.toMutableList()
        mutable[index] = player
        return mutable.toList()
    }

    fun nextPlayer(player: Player): Player = if (player == Player.X) Player.O else Player.X
}

