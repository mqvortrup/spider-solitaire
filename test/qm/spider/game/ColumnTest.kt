package qm.spider.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import qm.spider.cards.*


internal class ColumnTest {

    private var emptyColumn = Column()
    private var columnWithCards = Column()
    private var columnWithAlmostFullSuit = Column()

    @BeforeEach
    fun setUp() {
        emptyColumn = Column()
        emptyColumn.deal(listOf(EmptyCard))
        columnWithCards = Column()
        columnWithCards.deal(listOf(EmptyCard, RealCard(Suit.HEARTS, Value.TWO), RealCard(Suit.CLUBS, Value.KING)))
        columnWithAlmostFullSuit = Column()
        columnWithAlmostFullSuit.deal(listOf(EmptyCard))
        columnWithAlmostFullSuit.add(listOf(
                RealCard(Suit.HEARTS, Value.KING), RealCard(Suit.HEARTS, Value.QUEEN), RealCard(Suit.HEARTS, Value.JACK),
                RealCard(Suit.HEARTS, Value.TEN), RealCard(Suit.HEARTS, Value.NINE), RealCard(Suit.HEARTS, Value.EIGHT),
                RealCard(Suit.HEARTS, Value.SEVEN), RealCard(Suit.HEARTS, Value.SIX), RealCard(Suit.HEARTS, Value.FIVE),
                RealCard(Suit.HEARTS, Value.FOUR), RealCard(Suit.HEARTS, Value.THREE), RealCard(Suit.HEARTS, Value.TWO)
        ))
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

    @Test
    fun completingAFullSuit() {
        val fullSuit = columnWithAlmostFullSuit.add(listOf(RealCard(Suit.HEARTS, Value.ACE)))
        assertTrue(fullSuit)
        if (fullSuit) columnWithAlmostFullSuit.removeFullSuitWithReveal()
        assertEquals(0, columnWithAlmostFullSuit.topCardIndex())
    }
}