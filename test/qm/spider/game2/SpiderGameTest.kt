package qm.spider.game2

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class SpiderGameTest {

    @Test
    fun state() {
        val game = SpiderSolitaire.dealNewGame(shuffle = false)
        val initialState = game.state()
        game.moveCards(game.columns[0], game.columns[1], 1)
        val stateAfterMove = game.state()
        assertNotEquals(initialState, stateAfterMove, "state must not be same after moving a card")
        game.moveCards(game.columns[1], game.columns[0], 1)
        val stateAfterMoveBack = game.state()
        assertEquals(initialState, stateAfterMoveBack, "state must be same again after moving card back")
    }

    @Test
    fun dealFromStack() {
    }
}