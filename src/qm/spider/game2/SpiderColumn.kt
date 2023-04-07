package qm.spider.game2

import qm.spider.cards.*

class SpiderColumn() {
    private val stack = mutableListOf<Card>(EmptyCard)

    fun dealHidden(cards: List<Card>) {
        cards.forEach { card -> card.visible = false }
        stack.addAll(cards)
    }

    fun addVisible(cards: List<Card>) {
        cards.forEach { card -> card.visible = true }
        stack.addAll(cards)
    }

    fun isFullSuitVisible(): Boolean {
        val candidateSuit = stack.reversed().filter { card -> card.visible }
        if (candidateSuit.size < 13) return false
        if (candidateSuit.first().value != Value.ACE) return false
        for (index in 1..12) {
            if (! candidateSuit[index-1].followedByWithinSuit(candidateSuit[index])) return false
        }
        return true
    }

    fun revealTopCard() {
        stack[topCardIndex()].visible = true
    }

    override fun toString(): String {
        var result = String()
        for (index in 1 until stack.size) {
            result += if (stack[index].visible)
                (stack[index].toString() + " ")
            else
                "X "
        }
        return result
    }

    private fun longestSuit(): IntRange {
        val end = topCardIndex()
        var begin = end
        while (stack[begin-1] != EmptyCard && stack[begin-1].visible && stack[begin].followedByWithinSuit(stack[begin-1]))
            begin -= 1
        return IntRange(begin, end)
    }

    private fun topCardIndex() = stack.size - 1

    fun canAccept(candidateCard: Card): Boolean {
        return candidateCard.followedByOutsideSuit(topCard())
    }

    fun topCard(): Card {//= stack.last()
        return nthCard(0)
    }

    fun bottomCard(): Card {
        return stack[1]
    }

    fun nthCard(i: Int): Card {
        return stack[stack.size - 1 - i]
    }

    fun hideTopCard() {
        stack[topCardIndex()].visible = false
    }

    fun isCleared() = topCard() == EmptyCard

    fun takeTopCard(): Card {
        return stack.takeLast()
    }

    fun isTopCardVisible(): Boolean {
        return stack[topCardIndex()].visible
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

        return stack.zip(other.stack).all {pair -> pair.first == pair.second }
    }

    override fun hashCode(): Int {
        stack.fold(0) {hash, card -> hash + card.hashCode() }
        return stack.hashCode()
    }
}