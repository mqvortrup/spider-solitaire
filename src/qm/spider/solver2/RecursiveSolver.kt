package qm.spider.solver2

import java.util.*

class RecursiveSolver(private val game: BaseGame) {
    private val solution = Stack<BaseMove>()
    private val allStates = mutableSetOf<GameState>()

    private fun hasSolution(game: BaseGame) : Boolean {
        if (game.isSolved()) return true
        if (allStates.contains(game.state())) return false
        val moves = game.getPossibleMoves()
        if (moves.isEmpty()) return false
        moves.forEach { move ->
            doMove(move, game)
            if (hasSolution(game)) return true
            else undoMove()
        }
        return false
    }

    private fun undoMove() {
        game.undoMove(solution.pop())
    }

    private fun doMove(move: BaseMove, game: BaseGame) {
        solution.push(move)
        println("Solution size ${solution.size}")
        game.doMove(move)
        allStates.add(game.state())
    }

    fun findSolution() : List<BaseMove> {
        return if (hasSolution(game)) solution
               else emptyList()
    }
}