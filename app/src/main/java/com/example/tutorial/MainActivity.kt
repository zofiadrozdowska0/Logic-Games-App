package com.example.tutorial

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorial.models.BoardSize

private lateinit var rvBoard: RecyclerView
private lateinit var tvNumElements: TextView
private lateinit var tvNumPoints: TextView
private lateinit var tvText: TextView
private var boardSize: BoardSize = BoardSize.Hard

class MainActivity : ComponentActivity() {
    private lateinit var memoryAdapter: MemoryAdapter
    private lateinit var memoryGame: MemoryGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumElements = findViewById(R.id.tvNumElements)
        tvNumPoints = findViewById(R.id.tvNumPoints)
        tvText = findViewById(R.id.tvText)
        memoryGame = MemoryGame(boardSize)
        memoryAdapter = MemoryAdapter(this, boardSize, memoryGame, rvBoard, tvNumElements, tvText)

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
