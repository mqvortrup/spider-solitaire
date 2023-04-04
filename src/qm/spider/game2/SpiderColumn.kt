package qm.spider.game2

import qm.spider.cards.*

class SpiderColumn() {
    private val stack = mutableListOf<Card>(EmptyCard)
    private var visibleFrom: Int = topCardIndex()

    fun deal(cards: List<Card>): SpiderColumn {
        stack.addAll(cards)
        visibleFrom = topCardIndex()
        return this
    }

    fun add(cards: List<Card>): Boolean {
        stack.addAll(cards)
        return isFullSuitVisible()
    }

    fun isFullSuitVisible(): Boolean {
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
        return "$result, $visibleFrom"
    }

    private fun longestSuit(): IntRange {
        val end = topCardIndex()
        var begin = end
        while (begin-1 >= visibleFrom && stack[begin].followedByWithinSuit(stack[begin-1]))
            begin -= 1
        return IntRange(begin, end)
    }

    private fun topCardIndex() = stack.size - 1

    fun canAccept(candidateCard: Card): Boolean {
        return candidateCard.followedByOutsideSuit(topCard())
    }

    fun topCard() = stack.last()

    fun hideTopCard() {
        visibleFrom += 1
    }

    fun isCleared() = topCard() == EmptyCard

    fun takeTopCard(): Card {
        return stack.takeLast()
    }

    fun isTopCardVisible(): Boolean {
        return visibleFrom <= topCardIndex()
    }

    fun removeTop(count: Int): List<Card> {
        return stack.takeLastCount(count)
    }

    fun streak(): List<Card> {
        return if (! isCleared()) {
            val streakRange = longestSuit()
            stack.subList(streakRange.first, streakRange.last + 1)
        } else emptyList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpiderColumn

        if (stack != other.stack) return false
        if (visibleFrom != other.visibleFrom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + visibleFrom
        return result
    }
}