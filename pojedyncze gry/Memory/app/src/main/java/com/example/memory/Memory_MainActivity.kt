package com.example.memory

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memory.memory_models.BoardSize
import com.example.memory.memory_models.MemoryGame

class Memory_MainActivity : ComponentActivity() {

    private lateinit var adapter: Memory_MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame
    // private lateinit var clRoot: ConstraintLayout
    private lateinit var board: RecyclerView
    private lateinit var numMoves: TextView

    private var boardSize: BoardSize = BoardSize.HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memory_activity_main)

        board = findViewById(R.id.rvBoard)
        //clRoot = findViewById(R.id.clRoot)
        numMoves = findViewById(R.id.textViewNumMoves)

        memoryGame = MemoryGame(boardSize)

        board.layoutManager = GridLayoutManager(this, boardSize.getWidth())
        adapter = Memory_MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: Memory_MemoryBoardAdapter.CardClickListener {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        board.adapter = adapter
        board.setHasFixedSize(true)
    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.isWon()) {
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            return
        }
        if (memoryGame.flipCard(position)) {

            if (memoryGame.isWon()) {
                //dodaj ikone restartu
            }
        }
        numMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}
