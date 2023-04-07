package qm.spider.game2

import qm.spider.cards.Decks
import qm.spider.cards.Stack
import qm.spider.cards.Suit
import qm.spider.cards.takeLastCount
import qm.spider.solver2.*

class SpiderGame(val columns: List<SpiderColumn>, val stack: Stack, val discards: Stack): BaseGame() {

    override fun getPossibleMoves(): MutableList<BaseMove> {
        val result = mutableListOf<BaseMove>()
        columns.forEach { from ->
            val streak = from.streak()
            columns.filterNot { it === from }.forEach { to ->
                for (cardIndex in streak.indices) {
                    if (to.canAccept(streak[cardIndex])) {
                        result.add(
                            CardMove(this, from, to,
                                to.topCard().isSameSuit(streak[cardIndex]),
                                cardIndex == 0,
                                streak.size - cardIndex))
                    }
                }
            }
        }
        result.sortWith(compareBy (
            { (it as CardMove).sameSuit},
            { (it as CardMove).fullStreak},
            { (it as CardMove).count }
        ))
        result.reverse()
        if (stack.isNotEmpty()) result.add(DealMove(this))
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
            column.addVisible(listOf(card))
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
        val toMove = from.removeTop(count).reversed()
        to.addVisible(toMove)
    }

    fun columnIndex(column: SpiderColumn): Int {
        return columns.indexOf(column)
    }
}

object SpiderSolitaire {
    private fun getNewSpiderDeck(shuffle : Boolean = true): Stack {
        val deck = Decks.getFullSuit(Suit.HEARTS)
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.HEARTS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        deck.addAll(Decks.getFullSuit(Suit.CLUBS))
        if (shuffle) deck.shuffle()
        return deck
    }

    private fun initiallyFillColumn(count: Int, fromStack: Stack): SpiderColumn {
        val column = SpiderColumn()
        column.dealHidden(fromStack.takeLastCount(count))
        column.revealTopCard()
        return column
    }

    fun dealNewGame(shuffle : Boolean = true): SpiderGame {
        val deck = getNewSpiderDeck(shuffle)
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