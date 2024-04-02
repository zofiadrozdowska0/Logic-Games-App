package com.example.tutorial

import com.example.tutorial.models.BoardSize
import kotlin.random.Random

class MemoryGame(private val boardSize: BoardSize) {
    private val allCards: List<Int>
    private var numPairsFound = 0
    private var numCardsFlipped = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        val numCards = boardSize.getNumPairs()
        val random = Random(System.currentTimeMillis())
        allCards = List(numCards) { (it % (numCards / 2)) }
        allCards.shuffle(random)
    }

    fun flipCard(position: Int): Boolean {
        // logic to flip card
        return false
    }

    fun isCardFaceUp(position: Int): Boolean {
        return true
    }

    fun isGameOver(): Boolean {
        return false
    }

    fun getCardContent(position: Int): Int {
        return allCards[position]
    }

    fun getNumCards(): Int {
        return boardSize.getNumPairs() * 2
    }

    fun generateSequence() {
        // generate game sequence
    }
}
