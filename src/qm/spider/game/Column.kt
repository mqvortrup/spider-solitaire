package qm.spider.game

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

    fun isCleared() = stack.isEmpty()
}