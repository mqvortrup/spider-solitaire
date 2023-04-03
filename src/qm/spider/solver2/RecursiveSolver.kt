package qm.spider.solver2

import java.util.*

class RecursiveSolver(private val game: BaseGame) {
    private val solution = Stack<BaseMove>()
    private val allStates = mutableSetOf<GameState>()
    var movesTested = 0

    private fun hasSolution(game: BaseGame) : Boolean {
        println(game)
        if (game.isSolved()) return true
        val moves = game.getPossibleMoves()
        println("possible moves: ${moves}")
        if (moves.isEmpty()) return false
        moves.forEach { move ->
            doMove(move, game)
            if (allStates.contains(game.state())) {
                println("been there")
                undoMove()
            } else {
                allStates.add(game.state())
                if (hasSolution(game)) return true
                else undoMove()
            }
        }
        return false
    }

    private fun undoMove() {
        game.undoMove(solution.pop())
    }

    private fun doMove(move: BaseMove, game: BaseGame) {
        solution.push(move)
        movesTested++
        println("Solution ${solution}")
        game.doMove(move)
    }

    fun findSolution() : List<BaseMove> {
        return if (hasSolution(game)) solution
               else emptyList()
    }
}