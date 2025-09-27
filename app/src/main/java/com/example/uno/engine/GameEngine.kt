package com.example.uno.engine



import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uno.model.*
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class UnoGame(private val players: List<Player>) {
    data class State(
        val players: MutableList<Player>,
        val deck: MutableList<Card>,
        val discard: MutableList<Card>,
        var currentPlayerIndex: Int = 0,
        var direction: Int = 1,
        var winnerId: String? = null
    )

    val state = State(
        players = players.toMutableList(),
        deck = createDeck().shuffled(Random).toMutableList(),
        discard = mutableListOf()
    )

    init {
        deal()
        placeFirstCard()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun deal() {
        repeat(7) {
            state.players.forEach { p ->
                if (state.deck.isEmpty()) reshuffle()
                p.hand.add(state.deck.removeLast())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun placeFirstCard() {
        var first = state.deck.removeLast()
        while (first.value == Value.WILD || first.value == Value.WILD_DRAW_FOUR) {
            state.deck.add(0, first)
            first = state.deck.removeLast()
        }
        state.discard.add(first)
    }

    fun currentPlayer(): Player = state.players[state.currentPlayerIndex]

    private fun nextIndex(offset: Int = 1): Int =
        (state.currentPlayerIndex + state.direction * offset + state.players.size) % state.players.size
    private fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)

        // Number cards (0-9) and action cards (Skip, Reverse, Draw Two)
        for (color in colors) {
            deck.add(Card(color, Value.ZERO))                 // One zero
            for (i in 1..9) {                                  // Two of each 1-9
                val v = Value.values()[i]                      // ONE to NINE
                deck.add(Card(color, v))
                deck.add(Card(color, v))
            }
            // Action cards (two each)
            deck.add(Card(color, Value.SKIP))
            deck.add(Card(color, Value.SKIP))
            deck.add(Card(color, Value.REVERSE))
            deck.add(Card(color, Value.REVERSE))
            deck.add(Card(color, Value.DRAW_TWO))
            deck.add(Card(color, Value.DRAW_TWO))
        }

        // Wild cards
        repeat(4) {
            deck.add(Card(Color.WILD, Value.WILD))
            deck.add(Card(Color.WILD, Value.WILD_DRAW_FOUR))
        }

        return deck
    }

    fun playCard(playerId: String, card: Card): Boolean {
        val player = state.players.find { it.id == playerId } ?: return false
        val top = state.discard.last()
        if (card.color == top.color || card.value == top.value || card.color == Color.WILD) {
            val found = player.hand.find { it.color == card.color && it.value == card.value } ?: return false
            player.hand.remove(found)
            state.discard.add(card)
            applyEffect(card)
            if (player.hand.isEmpty()) state.winnerId = playerId
            else if (card.value != Value.SKIP) state.currentPlayerIndex = nextIndex(1)
            return true
        }
        return false
    }

    fun drawCard(playerId: String) {
        val player = state.players.find { it.id == playerId } ?: return
        if (state.deck.isEmpty()) reshuffle()
        player.hand.add(state.deck.removeLast())
        // after drawing, next player's turn
        state.currentPlayerIndex = nextIndex(1)
    }

    private fun applyEffect(card: Card) {
        when (card.value) {
            Value.SKIP -> state.currentPlayerIndex = nextIndex(2)
            Value.REVERSE -> {
                state.direction *= -1
                state.currentPlayerIndex = nextIndex(1)
            }
            Value.DRAW_TWO -> {
                val idx = nextIndex(1)
                repeat(2) {
                    if (state.deck.isEmpty()) reshuffle()
                    state.players[idx].hand.add(state.deck.removeLast())
                }
                state.currentPlayerIndex = nextIndex(2)
            }
            Value.WILD_DRAW_FOUR -> {
                val idx = nextIndex(1)
                repeat(4) {
                    if (state.deck.isEmpty()) reshuffle()
                    state.players[idx].hand.add(state.deck.removeLast())
                }
                state.currentPlayerIndex = nextIndex(2)
            }
            else -> {}
        }
    }

    private fun reshuffle() {
        val last = state.discard.removeLast()
        state.deck.addAll(state.discard.shuffled())
        state.discard.clear()
        state.discard.add(last)
    }
}
