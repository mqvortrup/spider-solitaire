package qm.spider.game

class Game(val columns: List<Column>, val stack: Stack, val discards: Stack) {

    fun getPossibleMoves(): MutableList<ColumnMove> {
        val result = mutableListOf<ColumnMove>()
        for (fromColumn in 0 until columns.size) {
            var fullMove = true
            for ((candidateCard, candidateIndex) in columns[fromColumn].movableCards()) {
                for (toColumn in 0 until columns.size) {
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
        result.sortWith(compareBy(ColumnMove::fullMove, ColumnMove::sameSuit, ColumnMove::value))
        result.reverse()
        return result
    }

    fun executeMove(move: Move) = when(move) {
        is ColumnMove -> {
            move.result = moveTo(columns[move.fromColumn], move.fromIndex, columns[move.toColumn])
            if (move.result.suitRemoved) {
                val fullSuit = columns[move.toColumn].removeFullSuit()
                discards.addAll(fullSuit)
            } else noop()
        }
        is DealMove -> noop()
        is NoValidMove -> noop()
    }

    fun undoMove(move: Move) = when(move) {
        is ColumnMove -> {
            if (move.result.cardRevealed)
                columns[move.fromColumn].hideTopCard()
            if (move.result.suitRemoved) {
                val fullSuit = discards.takeLastCount(13)
                columns[move.toColumn].add(fullSuit)
            }
            moveTo(columns[move.toColumn], move.toIndex, columns[move.fromColumn])
        }
        is DealMove -> noop()
        is NoValidMove -> noop()
    }

    fun moveTo(fromColumn: Column, fromIndex: Int, toColumn: Column): MoveResult {
        val toMove = fromColumn.takeLastFrom(fromIndex)
        val revealed = fromColumn.revealTopCard()
        val fullSuitVisible = toColumn.add(toMove)
        return MoveResult(revealed, fullSuitVisible)
    }


    fun deal() {
        for (index in 0 until columns.size) {
            val card = stack.removeAt(stack.size-1)
            columns[index].add(listOf(card))
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
}

class Column() {
    private val stack = mutableListOf<Card>()
    private var visibleFrom: Int = topCardIndex()

    fun deal(cards: List<Card>): Column {
        stack.addAll(cards)
        visibleFrom = topCardIndex()
        return this
    }

    fun add(cards: List<Card>): Boolean {
        stack.addAll(cards)
        return isFullSuitVisible()
    }

    fun removeFullSuit(): Stack {
        val suit = stack.takeLastCount(13)
        revealTopCard()
        return suit
    }

    private fun isFullSuitVisible(): Boolean {
        if (topCard().value != Value.ACE)
            return false
        else {
            var thisCard = topCardIndex()
            while (visibleCardFollowsInSuit(thisCard)) {
                if (stack[thisCard-1].value == Value.KING) return true
                thisCard -= 1
            }
            return false
        }
    }

    private fun visibleCardFollowsInSuit(thisCard: Int) =
        thisCard - 1 >= 0 && thisCard - 1 >= visibleFrom && stack[thisCard].followedByWithinSuit(stack[thisCard - 1])

    fun revealTopCard(): Boolean {
        if (visibleFrom == 1) return false
        if (visibleFrom >= stack.size) {
            visibleFrom = topCardIndex()
            return true
        } else
            return false
    }

    override fun toString(): String {
        var result = String()
        for (index in 1 until stack.size) {
            if (index < visibleFrom)
                result += "X "
            else
                result += (stack[index].toString() + " ")
        }
        return result + ", $visibleFrom"
    }

    fun movableCards(): MutableList<Pair<Card, Int>> {
        val result = mutableListOf<Pair<Card, Int>>()
        if (stack.size > 1)
            for (index in longestSuit())
                result.add(Pair(stack[index], index))
        return result
    }

    private fun longestSuit(): IntRange {
        val end = topCardIndex()
        var begin = end
        while (begin-1 >= visibleFrom && stack[begin].followedByWithinSuit(stack[begin-1]))
            begin -= 1
        return IntRange(begin, end)
    }

    fun topCardIndex() = stack.size - 1

    fun canAccept(candidateCard: Card): Boolean {
        return candidateCard.followedByOutsideSuit(topCard())
    }

    fun topCard() = stack.last()

    fun nextFree(): Int {
        return stack.size
    }

    fun hideTopCard() {
        visibleFrom += 1
    }

    fun takeLastFrom(fromIndex: Int): List<Card> {
        return stack.takeLastFrom(fromIndex)
    }
}

data class MoveResult(val cardRevealed: Boolean, val suitRemoved: Boolean)

sealed class Move
data class ColumnMove(
    val fromColumn: Int,
    val fromIndex: Int,
    val toColumn: Int,
    val toIndex: Int,
    val sameSuit: Boolean,
    val value: Int,
    val fullMove: Boolean
) : Move() {
    var result: MoveResult = MoveResult(false, false)
    override fun toString(): String {
        return "($fromColumn,$fromIndex -> $toColumn,$toIndex; $sameSuit; ${Value.values()[value]}; $fullMove)"
    }
}
object DealMove : Move()
object NoValidMove : Move()

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
        deck.shuffle()
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