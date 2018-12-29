package qm.spider.solver

import qm.spider.game.ColumnMove
import qm.spider.game.Game
import qm.spider.game.Move
import qm.spider.game.Spider

class Solver(val game: Game) {

    var moveStack = MoveStack()
    var countMoves = 0

    fun solve() {
        initializeWithFirstMoves()
        println(game)
        while (doMove()) {
            println(game)
        }
    }

    private fun initializeWithFirstMoves() {
        moveStack = MoveStack(moveStack, game.getPossibleMoves())
    }

    private fun doMove(): Boolean {
        countMoves ++
        if (moveStack.hasNextMove()) {
            game.executeMove(moveStack.getNextMove())
            return true
        }
        else
            return false
    }
}

open class MoveStack(val parent: MoveStack? = null, val possibleMoves: MutableList<ColumnMove> = mutableListOf()) {
    private var currentMove = 0


    fun getNextMove(): Move {
        return possibleMoves[currentMove++]
    }

    fun hasNextMove(): Boolean {
        return currentMove < possibleMoves.size
    }

}

fun main(args: Array<String>) {
    val solver = Solver(Spider.dealNewGame())
    solver.solve()
}