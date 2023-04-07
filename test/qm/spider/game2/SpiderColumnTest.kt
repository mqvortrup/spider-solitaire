package qm.spider.game2

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import qm.spider.cards.*

class SpiderColumnTest {

    @Test
    fun correctSequenceOnAdding() {
        val column = columnWithFullVisibleSuit()
        assertEquals(RealCard(Suit.HEARTS, Value.KING, true), column.topCard())
        assertEquals(RealCard(Suit.HEARTS, Value.ACE, true), column.bottomCard())
    }

    @Test
    fun correctSequenceOnRemovingAndAddingBack() {
        val column = columnWithFullVisibleSuit()
        val removed = column.removeTop(2)
        assertEquals(RealCard(Suit.HEARTS, Value.KING, true), removed[0])
        assertEquals(RealCard(Suit.HEARTS, Value.QUEEN, true), removed[1])
        column.addVisible(removed.reversed())
        assertEquals(RealCard(Suit.HEARTS, Value.KING, true), column.topCard())
        assertEquals(RealCard(Suit.HEARTS, Value.QUEEN, true), column.nthCard(1))
    }

    private fun columnWithFullVisibleSuit(): SpiderColumn {
        val column = SpiderColumn()
        column.addVisible(Decks.getFullSuit(Suit.HEARTS))
        return column
    }
}