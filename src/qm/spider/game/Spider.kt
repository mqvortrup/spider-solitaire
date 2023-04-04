package qm.spider.game

import qm.spider.cards.*
import qm.spider.solver2.BaseGame
import qm.spider.solver2.BaseMove
import qm.spider.solver2.GameState

class Game(val columns: List<Column>, val stack: Stack, val discards: Stack) {

    fun getPossibleMoves(): MutableList<Move> {
        val result = mutableListOf<Move>()
        for (fromColumn in columns.indices) {
            var fullMove = true
            for ((candidateCard, candidateIndex) in columns[fromColumn].movableCards()) {
                for (toColumn in columns.indices) {
                    if (toColumn != fromColumn) {
                        if (columns[toColumn].canAccept(candidateCard)) {
                            result.add(
                                ColumnMove(
                                    fromColumn, candidateIndex, toColumn, columns[toColumn].nextFree(),
                                    columns[toColumn].topCard().isSameSuit(candidateCard),
                                    candidateCard.value.ordinal,
                                    fullMove)
                            )
                        }
                    }
                }
                fullMove = false
            }
        }
        result.add(DealMove)
        //result.sortWith(compareBy(Move::fullMove, Move::sameSuit, Move::value))
        //result.reverse()
        return result
    }


    fun executeMove(move: Move) = when(move) {
        is ColumnMove -> {
            move.result = moveTo(columns[move.fromColumn], move.fromIndex, columns[move.toColumn])
            if (move.result.suitRemoved) {
                val fullSuit = columns[move.toColumn].removeFullSuitWithReveal()
                discards.addAll(fullSuit)
            } else noop()
        }
        is DealMove -> dealFromStack()
    }

    fun reverseMove(move: Move) = when(move) {
        is ColumnMove -> {
            if (move.result.cardRevealed)
                columns[move.fromColumn].hideTopCard()
            if (move.result.suitRemoved) {
                val fullSuit = discards.takeLastCount(13)
                columns[move.toColumn].add(fullSuit)
            }
            moveTo(columns[move.toColumn], move.toIndex, columns[move.fromColumn])
        }
        is DealMove -> reverseDealFromStack()
    }

    private fun moveTo(fromColumn: Column, fromIndex: Int, toColumn: Column): MoveResult {
        val toMove = fromColumn.takeLastFrom(fromIndex)
        val revealed = fromColumn.revealTopCard()
        val fullSuitVisible = toColumn.add(toMove)
        return MoveResult(revealed, fullSuitVisible)
    }


    fun dealFromStack() {
        for (column in columns) {
            val card = stack.removeAt(stack.size-1)
            column.add(listOf(card))
        }
    }

    fun reverseDealFromStack() {
        for (column in columns.reversed()) {
            stack.add(column.takeTopCard())
        }
    }

    private fun noop() {
        // do nothing
    }

    override fun toString(): String {
        var result = String()
        for (index in 0 until columns.size) result += ("\n  $index   ${columns[index]}")
        return result
    }

    fun stackHasMoreCards(): Boolean {
        return stack.size > 0
    }

    fun moveCards(from: Column, to: Column, count: Int) {
        val toMove = from.removeTop(count)
        to.add(toMove)
    }
}

data class MoveResult(val cardRevealed: Boolean, val suitRemoved: Boolean)

sealed class Move {
    abstract val sameSuit: Boolean
    abstract val fullMove: Boolean
    abstract val value: Int
    abstract var result: MoveResult
}

data class ColumnMove(
    val fromColumn: Int,
    val fromIndex: Int,
    val toColumn: Int,
    val toIndex: Int,
    override val sameSuit: Boolean,
    override val value: Int,
    override val fullMove: Boolean
) : Move() {
    override var result: MoveResult = MoveResult(false, false)
    override fun toString(): String {
        return "($fromColumn,$fromIndex -> $toColumn,$toIndex; $sameSuit; ${Value.values()[value]}; $fullMove)"
    }
}

object DealMove : Move() {
    override val sameSuit: Boolean
        get() = false
    override val fullMove: Boolean
        get() = false
    override val value: Int
        get() = 0
    override var result = MoveResult(false, false)
}

object Spider {
    private fun getNewSpiderDeck(): Stack {
        val deck = Decks.getFullSuit(Suit.HEARTS)
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        //deck.shuffle()
        return deck
    }

    private fun initiallyFillColumn(count: Int, fromStack: Stack): Column {
        val column = Column()
        column.add(listOf(EmptyCard))
        column.deal(fromStack.takeLastCount(count))
        return column
    }

    fun dealNewGame(): Game {
        val deck = getNewSpiderDeck()
        val columns = List<Column>(10) { index: Int ->
            if (index < 4) {
                initiallyFillColumn(6, deck)
            } else {
                initiallyFillColumn(5, deck)
            }
        }
        return Game(columns, deck, mutableListOf())
    }
}

data class SpiderState(val hash: Long) : GameState