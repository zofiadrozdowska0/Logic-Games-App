package com.example.signupapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private lateinit var rvBoard: RecyclerView
private lateinit var tvNumElements: TextView
private lateinit var tvNumPoints: TextView
private lateinit var tvText: TextView
private var boardSize: zap_sekwencja_BoardSize = zap_sekwencja_BoardSize.Hard

class zap_sekwencja_MainActivity : ComponentActivity() {
    private lateinit var memoryAdapter: zap_sekwencja_MemoryAdapter
    private lateinit var memoryGame: zap_sekwencja_MemoryGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zap_sekwencja_activity_main_screen)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumElements = findViewById(R.id.tvNumElements)
        tvNumPoints = findViewById(R.id.tvNumPoints)
        tvText = findViewById(R.id.tvText)
        memoryGame = zap_sekwencja_MemoryGame(boardSize)
        memoryAdapter = zap_sekwencja_MemoryAdapter(this, boardSize, memoryGame, rvBoard, tvNumElements, tvText)

        rvBoard.adapter = memoryAdapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

        startNewGame()
    }

    private fun startNewGame() {
        memoryGame.generateSequence()
        memoryAdapter.startGame()

    }

}
