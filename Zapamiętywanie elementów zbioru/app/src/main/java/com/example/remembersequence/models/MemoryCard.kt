package com.example.remembersequence.models

class MemoryCard(
    val pozycja: Int,
    val imageId: Int,
    var isMatched: Boolean = false,
    var isRevealed: Boolean = false
)