package qm.spider.solver2

import qm.spider.cards.takeLastCount
import qm.spider.game2.SpiderColumn
import qm.spider.game2.SpiderGame

abstract class SpiderMove: BaseMove, Action {
    private val consequences = mutableListOf<Action>()
    protected var discardSuit = false

    override fun performMove() {
        perform()
        consequences.addAll(collectConsequences())
    }

    protected abstract fun collectConsequences() : List<Action>

    override fun undoMove() {
        consequences.reversed().forEach {
            action -> action.undo()
        }
        undo()
    }

    protected fun checkForVisibleTopCard(game: SpiderGame, column: SpiderColumn): List<Action> {
        return if (!column.isTopCardVisible()) {
            val revealTopCard = RevealTopCardAction(column)
            revealTopCard.perform()
            mutableListOf(revealTopCard)
        } else {
            mutableListOf()
        }
    }

    protected fun checkForFullSuit(game: SpiderGame, column: SpiderColumn): List<Action> {
        var result = mutableListOf<Action>()
        if (column.isFullSuitVisible()) {
            discardSuit = true
            val removeFullSuit = RemoveFullSuitAction(game, column)
            removeFullSuit.perform()
            result.add(removeFullSuit)
            result.addAll(checkForVisibleTopCard(game, column))
        }
        return result
    }
}

class CardMove(val game: SpiderGame, val from: SpiderColumn, val to: SpiderColumn, val sameSuit: Boolean, val fullStreak: Boolean, val count: Int): SpiderMove() {
    private val receivingCard = to.topCard()
    private val movedCard = from.nthCard(count-1)
    private val fromIndex = game.columnIndex(from)
    private val toIndex = game.columnIndex(to)

    override fun collectConsequences(): List<Action> {
        var result = mutableListOf<Action>()
        result.addAll(checkForFullSuit(game, to))
        result.addAll(checkForVisibleTopCard(game, from))
        return result
    }

    override fun perform() {
        game.moveCards(from, to, count)
    }

    override fun undo() {
        game.moveCards(to, from, count)
    }

    override fun toString(): String {
        return "CardMove(fromIndex=$fromIndex, toIndex=$toIndex, movedCard=$movedCard, receivingCard=$receivingCard, count=$count, sameSuit=$sameSuit, fullStreak=$fullStreak, discardSuit=$discardSuit)"
    }
}

class DealMove(private val game: SpiderGame): SpiderMove() {
    private val cardsLeft = game.stack.size

    override fun collectConsequences() : List<Action> {
        var result = mutableListOf<Action>()
        game.columns.forEach {column ->
            result.addAll(checkForFullSuit(game, column))
        }
        return result
    }

    override fun perform() {
        game.dealFromStack()
    }

    override fun undo() {
        game.reverseDealFromStack()
    }

    override fun toString(): String {
        return "DealMove(cardsLeft=$cardsLeft, discardSuit=$discardSuit)"
    }
}

class RevealTopCardAction(private val column: SpiderColumn) : Action {
    override fun perform() {
        column.revealTopCard()
    }

    override fun undo() {
        column.hideTopCard()
    }
}

class RemoveFullSuitAction(private val game: SpiderGame, private val column: SpiderColumn): Action {
    override fun perform() {
        val fullSuit = column.removeTop(13)
        game.discards.addAll(fullSuit)
    }

    override fun undo() {
        val fullSuit = game.discards.takeLastCount(13)
        column.addVisible(fullSuit)
    }
}

interface Action {
    fun perform()

    fun undo()
}