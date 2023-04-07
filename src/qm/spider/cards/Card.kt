package qm.spider.cards

typealias Stack = MutableList<Card>

abstract class Card {
    abstract val suit: Suit
    abstract val value: Value
    abstract var visible: Boolean
    abstract fun followedByOutsideSuit(otherCard: Card): Boolean
    abstract fun followedByWithinSuit(otherCard: Card): Boolean
    abstract fun isSameSuit(otherCard: Card): Boolean
}

data class RealCard(override val suit: Suit, override val value: Value, override var visible: Boolean = false) : Card() {
    override fun followedByOutsideSuit(otherCard: Card): Boolean {
        if (otherCard is EmptyCard) return true
        return otherCard is RealCard && this.value.isFollowedBy(otherCard.value)
    }

    override fun followedByWithinSuit(otherCard: Card): Boolean {
        if (otherCard is EmptyCard) return true
        return otherCard is RealCard && this.suit == otherCard.suit && this.value.isFollowedBy(otherCard.value)
    }

    override fun isSameSuit(otherCard: Card): Boolean {
        return otherCard is RealCard && this.suit == otherCard.suit
    }

    override fun toString(): String {
        return "($suit, $value)"
    }
}

object EmptyCard : Card() {
    override val suit = Suit.ANY
    override val value = Value.ANY
    override var visible = false

    override fun followedByWithinSuit(otherCard: Card) = true

    override fun isSameSuit(otherCard: Card) = true

    override fun followedByOutsideSuit(otherCard: Card) = true

    override fun toString() = "-"
}

enum class Value {
    ANY {
        fun equals(other: Value) = true
    },

    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING,

    NONE {
        fun equals(other: Value) = false
    };

    fun isFollowedBy(other: Value): Boolean {
        return this != KING && this.ordinal + 1 == other.ordinal
    }
}

fun Value.range() = arrayOf(
    Value.ACE,
    Value.TWO,
    Value.THREE,
    Value.FOUR,
    Value.FIVE,
    Value.SIX,
    Value.SEVEN,
    Value.EIGHT,
    Value.NINE,
    Value.TEN,
    Value.JACK,
    Value.QUEEN,
    Value.KING
)

enum class Suit {
    ANY{
        fun equals(other: Value) = true
    },

    HEARTS, DIAMONDS, CLUBS, SPADES,

    NONE {
        fun equals(other: Value) = false
    };
}

fun Suit.range() = arrayOf(Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS, Suit.SPADES)

object Decks {
    fun getFullSuit(suit: Suit): Stack {
        val newSuit = mutableListOf<Card>()
        for (value in Value.ACE.range())
            newSuit.add(RealCard(suit, value))
        return newSuit

    }

    fun getFreshDeck(): Stack {
        val newDeck = mutableListOf<Card>()
        for (suit in Suit.HEARTS.range()) {
            newDeck.addAll(getFullSuit(suit))
        }
        newDeck.shuffle()
        return newDeck
    }
}

fun Stack.takeLast(): Card {
    return this.removeAt(this.size-1)
}

//take the last count elements
fun Stack.takeLastCount(count: Int): Stack {
    val result = mutableListOf<Card>()
    for (i in 0 until count) result.add(this.takeLast())
    return result
}

//take the last elements, starting with from (incl)
fun Stack.takeLastFrom(from: Int): Stack {
    val result = mutableListOf<Card>()
    for (i in from until this.size) result.add(this.takeLast())
    result.reverse()
    return result
}