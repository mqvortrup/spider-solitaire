package qm.spider.game2

import qm.spider.cards.*
import qm.spider.solver2.*

class SpiderGame(val columns: List<SpiderColumn>, val stack: Stack, val discards: Stack): BaseGame() {

    override fun getPossibleMoves(): MutableList<BaseMove> {
        val result = mutableListOf<BaseMove>()
        columns.forEach { from ->
            val streak = from.streak()
            for (cardIndex in streak.indices) {
                columns.filter { it != from }.forEach { to ->
                    if (to.canAccept(streak[cardIndex])) {
                        result.add(CardMove(this, from, to, streak.size - cardIndex))
                    }
                }
            }
        }
        result.add(DealMove(this))
        return result
    }
    override fun isSolved() = columns.all { column -> column.isCleared() }

    override fun state() =
        SpiderState(
            columns.foldIndexed(0L) { index, total, column ->
                total + index*column.hashCode()
            }
        )

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

    override fun toString(): String {
        var result = String()
        for (index in columns.indices) result += ("\n  $index   ${columns[index]}")
        return result
    }

    fun moveCards(from: SpiderColumn, to: SpiderColumn, count: Int) {
        val toMove = from.removeTop(count)
        to.add(toMove)
    }
}

object SpiderSolitaire {
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

    private fun initiallyFillColumn(count: Int, fromStack: Stack): SpiderColumn {
        val column = SpiderColumn()
        column.deal(fromStack.takeLastCount(count))
        return column
    }

    fun dealNewGame(): SpiderGame {
        val deck = getNewSpiderDeck()
        val columns = List(10) { index: Int ->
            if (index < 4) {
                initiallyFillColumn(6, deck)
            } else {
                initiallyFillColumn(5, deck)
            }
        }
        return SpiderGame(columns, deck, mutableListOf())
    }
}

data class SpiderState(val hash: Long) : GameState