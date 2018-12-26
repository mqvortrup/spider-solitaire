package qm.spider.game

class Game(val columns: List<Column>, val stack: Stack) {

    fun getPossibleMoves(): MutableList<ColumnMove> {
        val result = mutableListOf<ColumnMove>()
        for (fromColumn in 0 until columns.size) {
            for ((candidateCard, candidateIndex) in columns[fromColumn].movableCards()) {
                for (toColumn in 0 until columns.size) {
                    if (toColumn != fromColumn) {
                        if (columns[toColumn].canAccept(candidateCard)) {
                            result.add(
                                ColumnMove(
                                    fromColumn, candidateIndex, toColumn, columns[toColumn].nextFree(),
                                    columns[toColumn].topCard().isSameSuit(candidateCard),
                                    candidateCard.value.ordinal)
                            )
                        }
                    }
                }
            }
        }
        result.sortWith(compareBy(ColumnMove::sameSuit, ColumnMove::value))
        result.reverse()
        return result
    }

    fun executeMove(move: Move) = when(move) {
        is ColumnMove -> columns[move.fromColumn].moveTo(move.fromIndex, columns[move.toColumn])
        is DealMove -> noop()
        is NoValidMove -> noop()
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
    private var visibleFrom: Int = stack.size - 1

    fun deal(cards: List<Card>) {
        stack.addAll(cards)
        visibleFrom = stack.size - 1
    }

    fun add(cards: List<Card>) {
        stack.addAll(cards)
        if (isFullSuitVisible()) removeFullSuit()
    }

    private fun removeFullSuit() {
        stack.takeLastCount(13)
        revealTopCard()
    }

    private fun isFullSuitVisible(): Boolean {
        if (topCard().value != Value.ACE)
            return false
        else {
            var thisCard = stack.size - 1
            while (thisCard - 1 >= 0 && thisCard-1 >= visibleFrom && stack[thisCard-1].followedByWithinSuit(stack[thisCard])) {
                if (stack[thisCard - 1].value == Value.KING) return true
                thisCard -= 1
            }
            return false
        }
    }

    private fun revealTopCard() {
        if (visibleFrom == 1) return
        if (visibleFrom >= stack.size)
            visibleFrom = stack.size - 1
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
        if (stack.size > 0)
            for (index in longestSuit())
                result.add(Pair(stack[index], index))
        return result
    }

    private fun longestSuit(): IntRange {
        val end = stack.size-1
        var begin = end
        while (begin-1 >= visibleFrom && stack[begin].followedByWithinSuit(stack[begin-1]))
            begin -= 1
        return IntRange(begin, end)
    }

    fun canAccept(candidateCard: Card): Boolean {
        return stack.isEmpty() || topCard().followedByOutsideSuit(candidateCard)
    }

    fun topCard() = stack.last()

    fun nextFree(): Int {
        return visibleFrom + 1
    }

    fun moveTo(fromIndex: Int, toColumn: Column) {
        val toMove = stack.takeLastFrom(fromIndex)
        resetVisible()
        toColumn.add(toMove)
    }

    private fun resetVisible() {
        if (visibleFrom >= stack.size) revealTopCard()
    }
}

sealed class Move
data class ColumnMove(
    val fromColumn: Int,
    val fromIndex: Int,
    val toColumn: Int,
    val toIndex: Int,
    val sameSuit: Boolean,
    val value: Int
) : Move() {
    override fun toString(): String {
        return "($fromColumn,$fromIndex -> $toColumn,$toIndex; $sameSuit; ${Value.values()[value]})"
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
        return Game(columns, deck)
    }
}