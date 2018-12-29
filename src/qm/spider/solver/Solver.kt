package qm.spider.solver

import qm.spider.game.*

class Solver(val game: Game) {

    var moveStack = MoveStack()
    var countMoves = 0

    fun solve() {
        initializeWithFirstMoves()
        println(game)
        while (doMove()) {
            //println(game)
            println(countMoves)
        }
        println(game)
    }

    private fun initializeWithFirstMoves() {
        moveStack = MoveStack(moveStack, game.getPossibleMoves())
    }

    private fun doMove(): Boolean {
        countMoves ++
        if (moveStack.hasNextMove()) {
            game.executeMove(moveStack.getNextMove())
            moveStack = MoveStack(moveStack, game.getPossibleMoves())
            return true
        } else if (game.stackHasMoreCards()) {
            println("Dealing")
            moveStack = MoveStack(moveStack, mutableListOf(DealMove))
            game.executeMove(moveStack.getNextMove())
            moveStack = MoveStack(moveStack, game.getPossibleMoves())
            return true
        }
        else
            return false
    }
}

open class MoveStack(val parent: MoveStack? = null, val possibleMoves: MutableList<Move> = mutableListOf()) {
    private var currentMove = 0


    fun getNextMove(): Move {
        return possibleMoves[currentMove++]
    }

    fun hasNextMove(): Boolean {
        return (currentMove < possibleMoves.size) && possibleMoves[currentMove+1].fullMove
    }

}

fun main(args: Array<String>) {
    val solver = Solver(Spider.dealNewGame())
    solver.solve()
}