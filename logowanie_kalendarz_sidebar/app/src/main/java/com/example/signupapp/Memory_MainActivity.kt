package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signupapp.memory_models.BoardSize
import com.example.signupapp.memory_models.MemoryGame
import android.content.Context

class Memory_MainActivity : ComponentActivity() {

    private lateinit var adapter: Memory_MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame
    private lateinit var board: RecyclerView
    private lateinit var numMoves: TextView

    private var boardSize: BoardSize = BoardSize.HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memory_activity_main)

        board = findViewById(R.id.rvBoard)
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
                val finalScore = calculateScore(memoryGame.getNumMoves())
                showCompletionDialog(finalScore)
                savePointsToSharedPreferences("memory_points", finalScore)

            }
        }
        numMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
    private fun savePointsToSharedPreferences(key: String, points: Int) {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, points)
        editor.apply()
    }
    private fun calculateScore(numMoves: Int): Int {
        val baseScore = 10
        val extraMoves = numMoves - 20
        val penalty = if (extraMoves > 0) (extraMoves / 3) else 0
        val finalScore = baseScore - penalty
        return if (finalScore < 0) 0 else finalScore
    }

    private fun showCompletionDialog(finalScore: Int) {
        AlertDialog.Builder(this)
            .setTitle("Game Completed")
            .setMessage("Your final score is $finalScore points.")
            .setPositiveButton("Next Game") { _, _ ->
                val intent = Intent(this, zap_sekwencja_MainActivity::class.java)
                intent.putExtra("FINAL_SCORE", finalScore)
                startActivity(intent)
            }
            .show()
    }
}
