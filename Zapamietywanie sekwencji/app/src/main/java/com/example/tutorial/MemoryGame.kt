package com.example.tutorial

import com.example.tutorial.models.BoardSize
import kotlin.random.Random

class MemoryGame(private val boardSize: BoardSize) {
    private val allCards: List<Int>
    private var numCardsFlipped = 0
    private var indexOfSingleSelectedCard: Int? = null
    var level: Int = 2
    init {
        val numCards = boardSize.numCards
        val half = numCards / 2
        val random = Random(System.currentTimeMillis())
        allCards = (0 until half).flatMap { listOf(it, it) }.shuffled(random)
    }


    fun generateSequence() {
        // Not using pairs, no need for sequence generation
    }
    fun getSequence(): List<Int> {
        // Tworzymy sekwencję losowych pozycji kart
        val sequence = mutableListOf<Int>()

        repeat(level) {
            sequence.add(Random.nextInt(boardSize.numCards))

        }

        return sequence
    }

}
