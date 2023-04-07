package qm.spider.solver

import qm.spider.game.DealMove
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
            //println(game)
            println(countMoves)
        }
        println(game)
    }

    private fun doMove(): Boolean {
        countMoves ++
        val (restartAt, previous) = moveStack.cycleAt()
        if (restartAt != null) {
            println("cycle detected")
            backtrackTo(restartAt, previous)
            return true
        } else if (moveStack.hasNextMove()) {
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

    private fun initializeWithFirstMoves() {
        moveStack = MoveStack(moveStack, game.getPossibleMoves())
    }

    private fun backtrackTo(restartAt: MoveStack, previous: MoveStack?) {
        previous?.parent = null
        moveStack = restartAt
    }
}

class MoveStack(var parent: MoveStack? = null, private val possibleMoves: MutableList<Move> = mutableListOf()) {
    private var currentMove = 0


    fun getNextMove(): Move {
        return possibleMoves[currentMove++]
    }

    fun hasNextMove(): Boolean {
        return (currentMove < possibleMoves.size - 1) && possibleMoves[currentMove+1].fullMove
    }

    fun cycleAt(): Pair<MoveStack?, MoveStack?> {
        var checkAgainst = parent
        var previous: MoveStack? = this
        while (checkAgainst != null && this.possibleMoves != checkAgainst.possibleMoves) {
            previous = checkAgainst
            checkAgainst = checkAgainst.parent
        }
        return Pair(checkAgainst, previous)
    }
}

fun main(args: Array<String>) {
    val solver = Solver(Spider.dealNewGame())
    solver.solve()
}