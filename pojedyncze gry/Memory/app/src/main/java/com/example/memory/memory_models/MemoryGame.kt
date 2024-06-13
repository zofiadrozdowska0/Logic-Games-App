package com.example.memory.memory_models

import com.example.memory.memory_utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    private var numPairsFound = 0

    private var numFlips = 0
    private var indexOfSelectedCard: Int? = null

    init {
        val gameImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedCards = (gameImages + gameImages).shuffled()
        cards = randomizedCards.map { MemoryCard(it)}
    }

    fun flipCard(position: Int): Boolean {
        numFlips++
        val card = cards[position]
        var foundMatch = false
        if (indexOfSelectedCard == null) {
            restoreCards()
            indexOfSelectedCard = position
        }
        else {
            foundMatch = checkForMatch(indexOfSelectedCard!!, position)
            indexOfSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound ++
        return true
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    fun isWon(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numFlips / 2
    }

}