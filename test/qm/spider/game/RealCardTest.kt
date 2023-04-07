package qm.spider.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import qm.spider.cards.*

internal class RealCardTest {

    @Test
    fun followedByOutsideSuit() {
        assertTrue(RealCard(Suit.CLUBS, Value.ACE).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.TWO)))
        assertTrue(RealCard(Suit.CLUBS, Value.TWO).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.THREE)))
        assertTrue(RealCard(Suit.CLUBS, Value.THREE).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.FOUR)))
        assertTrue(RealCard(Suit.CLUBS, Value.FOUR).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.FIVE)))
        assertTrue(RealCard(Suit.CLUBS, Value.FIVE).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.SIX)))
        assertTrue(RealCard(Suit.CLUBS, Value.SIX).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.SEVEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.SEVEN).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.EIGHT)))
        assertTrue(RealCard(Suit.CLUBS, Value.EIGHT).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.NINE)))
        assertTrue(RealCard(Suit.CLUBS, Value.NINE).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.TEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.TEN).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.JACK)))
        assertTrue(RealCard(Suit.CLUBS, Value.JACK).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.QUEEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.QUEEN).followedByOutsideSuit(RealCard(Suit.HEARTS, Value.KING)))
    }

    @Test
    fun followedByWithinSuit() {
        assertTrue(RealCard(Suit.CLUBS, Value.ACE).followedByWithinSuit(RealCard(Suit.CLUBS, Value.TWO)))
        assertTrue(RealCard(Suit.CLUBS, Value.TWO).followedByWithinSuit(RealCard(Suit.CLUBS, Value.THREE)))
        assertTrue(RealCard(Suit.CLUBS, Value.THREE).followedByWithinSuit(RealCard(Suit.CLUBS, Value.FOUR)))
        assertTrue(RealCard(Suit.CLUBS, Value.FOUR).followedByWithinSuit(RealCard(Suit.CLUBS, Value.FIVE)))
        assertTrue(RealCard(Suit.CLUBS, Value.FIVE).followedByWithinSuit(RealCard(Suit.CLUBS, Value.SIX)))
        assertTrue(RealCard(Suit.CLUBS, Value.SIX).followedByWithinSuit(RealCard(Suit.CLUBS, Value.SEVEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.SEVEN).followedByWithinSuit(RealCard(Suit.CLUBS, Value.EIGHT)))
        assertTrue(RealCard(Suit.CLUBS, Value.EIGHT).followedByWithinSuit(RealCard(Suit.CLUBS, Value.NINE)))
        assertTrue(RealCard(Suit.CLUBS, Value.NINE).followedByWithinSuit(RealCard(Suit.CLUBS, Value.TEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.TEN).followedByWithinSuit(RealCard(Suit.CLUBS, Value.JACK)))
        assertTrue(RealCard(Suit.CLUBS, Value.JACK).followedByWithinSuit(RealCard(Suit.CLUBS, Value.QUEEN)))
        assertTrue(RealCard(Suit.CLUBS, Value.QUEEN).followedByWithinSuit(RealCard(Suit.CLUBS, Value.KING)))
    }

    @Test
    fun everyCardIsFollowedByEmptyCard() {
        for (card in Decks.getFreshDeck()) {
            assertTrue(card.followedByOutsideSuit(EmptyCard))
            assertTrue(card.followedByWithinSuit(EmptyCard))
        }
    }
}