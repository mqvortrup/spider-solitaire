package qm.spider.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ColumnTest {

    private var emptyColumn = Column()
    private var columnWithCards = Column()

    @BeforeEach
    fun setUp() {
        emptyColumn = Column()
        emptyColumn.deal(listOf(EmptyCard))
        columnWithCards = Column()
        columnWithCards.deal(listOf(EmptyCard, RealCard(Suit.HEARTS, Value.TWO), RealCard(Suit.CLUBS, Value.KING)))
    }

    @Test
    fun movableCardsEmptyColumn() {
        val movables = emptyColumn.movableCards()
        assertEquals(0, movables.size)
    }

    @Test
    fun movableCardsOneCardShowing() {
        val movables = columnWithCards.movableCards()
        assertEquals(1, movables.size)
    }

    @Test
    fun movableCardsTwoCardsShowingDifferentSuitCorrectOrder() {
        columnWithCards.add(listOf(RealCard(Suit.HEARTS, Value.QUEEN)))
        val movables = columnWithCards.movableCards()
        assertEquals(1, movables.size)
    }

    @Test
    fun movableCardsTwoCardsShowingSameSuitCorrectOrder() {
        columnWithCards.add(listOf(RealCard(Suit.CLUBS, Value.QUEEN)))
        val movables = columnWithCards.movableCards()
        assertEquals(2, movables.size)
    }

    @Test
    fun canAcceptEmptyColumnAnyCard() {
        for (card in Decks.getFreshDeck())
            assertTrue(emptyColumn.canAccept(card))
    }
}