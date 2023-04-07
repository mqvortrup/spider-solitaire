package qm.spider.solver2

abstract class BaseGame {

    abstract fun getPossibleMoves() : List<BaseMove>

    abstract fun isSolved() : Boolean

    abstract fun state(): GameState
}

interface GameState {

}

interface BaseMove {

    fun performMove()

    fun undoMove()

}
