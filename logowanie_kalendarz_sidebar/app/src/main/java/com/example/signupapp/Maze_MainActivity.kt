package com.example.signupapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Button
import java.util.*
import java.util.concurrent.TimeUnit
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog
import android.content.Context

class Maze_MainActivity : Activity() {

    private lateinit var mazeView: Maze_MazeView
    private lateinit var ballView: View
    private lateinit var timerTextView: TextView
    private var offsetX = 0f
    private var offsetY = 0f
    private var gameOver = false
    private var level = 1
    private var isNewMazeChecked = false
    private var collisions = 0

    // Timer fields
    private lateinit var timer: CountDownTimer
    private var timeLeftInMillis: Long = 60000 // 1 minute in milliseconds
    private var timerRunning = false
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maze_main_activity)
        findViewById<TextView>(R.id.scoreTextView).visibility = View.GONE

        mazeView = findViewById(R.id.mazeView)
        ballView = findViewById(R.id.ballView)
        timerTextView = findViewById(R.id.timerTextView)

        // Retrieve the remaining time if passed
        timeLeftInMillis = intent.getLongExtra("TIME_LEFT", 60000)

        level = intent.getIntExtra("LEVEL", 1)
        findViewById<TextView>(R.id.levelTextView).text = "Level: $level"

        generateNewMaze()

        timer = createCountDownTimer(timeLeftInMillis)

        mazeView.setOnTouchListener { _, event ->
            if (!gameOver) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        offsetX = event.rawX - ballView.x
                        offsetY = event.rawY - ballView.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX - offsetX
                        val newY = event.rawY - offsetY
                        if (isInBounds(newX, newY)) {
                            ballView.x = newX
                            ballView.y = newY
                            checkForNewMaze(newX, newY, timeLeftInMillis) // Passing timeLeftInMillis
                        } else {
                            collisions++
                            restartGame()
                        }
                    }
                }
            }
            true
        }

        startTimer()
    }

    private fun disableGameInteraction() {
        mazeView.setOnTouchListener(null) // Disable onTouchListener
    }

    private fun generateNewMaze() {
        val maze = generateMaze(8, 8)
        if (maze != null) {
            mazeView.setMaze(convertToBooleanArray(maze), ballView)
            ballView.x = 0f // Reset ball position
            ballView.y = 0f
            gameOver = false // Reset game state
            findViewById<TextView>(R.id.gameOverTextView).visibility = View.INVISIBLE // Hide game over text
        } else {
            // If maze generation fails, try again
            generateNewMaze()
        }
    }

    private fun checkForNewMaze(newX: Float, newY: Float, timeLeftInMillis: Long) {
        // Check if ball reaches the treasure
        if (mazeView.isBallOnTreasure(newX, newY) && !isNewMazeChecked) {
            // Increase level
            level++
            isNewMazeChecked = true // Set flag to avoid multiple checks in one move
            // Update the label on the screen
            // Restart the game with the current timer value
            restartActivity(level, timeLeftInMillis)
        }
    }

    private fun restartGame() {
        disableGameInteraction()
        val intent = Intent(this, Maze_MainActivity::class.java)
        intent.putExtra("TIME_LEFT", timeLeftInMillis)
        intent.putExtra("LEVEL", level)
        intent.putExtra("COLLISIONS", collisions)
        startActivity(intent)
        finish() // Close current activity
    }

    private fun startTimer() {
        timerRunning = true
        timer.start()
    }

    private fun createCountDownTimer(timeLeft: Long): CountDownTimer {
        return object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                timerRunning = false
                gameOver = true
                val score = calculateScore(level, collisions)
                findViewById<TextView>(R.id.gameOverTextView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.scoreTextView).apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.score_message, score)
                }
                if (!isFinishing) {
                    showCompletionDialog(score)
                }
            }
        }
    }

    private fun updateCountdownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.timerTextView).text = timeLeftFormatted
    }

    private fun restartActivity(level: Int, timeLeftInMillis: Long) {
        disableGameInteraction()
        val intent = Intent(this, Maze_MainActivity::class.java)
        intent.putExtra("TIME_LEFT", timeLeftInMillis)
        intent.putExtra("LEVEL", level)
        intent.putExtra("COLLISIONS", collisions)
        startActivity(intent)
        finish()
    }

    private fun isInBounds(newX: Float, newY: Float): Boolean {
        val ballCenterX = newX + ballView.width / 2
        val ballCenterY = newY + ballView.height / 2
        return mazeView.checkCollision(ballCenterX, ballCenterY)
    }

    private fun convertToBooleanArray(maze: Array<IntArray>): Array<BooleanArray> {
        val booleanMaze = Array(maze.size) { BooleanArray(maze[0].size) }
        for (i in maze.indices) {
            for (j in maze[i].indices) {
                booleanMaze[i][j] = maze[i][j] == WALL
            }
        }
        return booleanMaze
    }

    private fun generateMaze(width: Int, height: Int): Array<IntArray>? {
        val maze: Array<IntArray> = Array(width) { IntArray(height) }
        // Fill the maze with walls
        for (x in 0 until width) {
            for (y in 0 until height) {
                maze[x][y] = WALL
            }
        }

        // Choose a random starting point
        val random = Random()
        val startX = random.nextInt(width)
        val startY = random.nextInt(height)

        // Generate the maze starting from the starting point
        generateMazeRecursive(maze, startX, startY)

        // Check if the maze contains at least 5 free spaces
        var freeSpaceCount = 0
        var singleNeighborCount = 0
        var singleNeighborX = 0
        var singleNeighborY = 0

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                if (maze[x][y] == EMPTY) {
                    freeSpaceCount++
                    if (countSurroundingSpaces(maze, x, y) == 1) {
                        singleNeighborCount++
                        singleNeighborX = x
                        singleNeighborY = y
                    }
                }
            }
        }

        // Check conditions on the number of free spaces with one neighbor
        if (freeSpaceCount >= 6 && singleNeighborCount == 2) {
            // If satisfied, return the generated maze
            return maze
        } else {
            // Otherwise, generate a new maze
            return generateMaze(width, height)
        }
    }

    private fun countSurroundingSpaces(maze: Array<IntArray>, x: Int, y: Int): Int {
        var count = 0
        if (maze[x - 1][y] == EMPTY) count++ // left
        if (maze[x + 1][y] == EMPTY) count++ // right
        if (maze[x][y - 1] == EMPTY) count++ // up
        if (maze[x][y + 1] == EMPTY) count++ // down
        return count
    }

    private fun generateMazeRecursive(maze: Array<IntArray>, x: Int, y: Int) {
        val directions = arrayOf(
            intArrayOf(-1, 0), // left
            intArrayOf(1, 0),  // right
            intArrayOf(0, -1), // up
            intArrayOf(0, 1)   // down
        )

        // Shuffle directions
        directions.shuffle()

        for (direction in directions) {
            val newX = x + direction[0]
            val newY = y + direction[1]

            // Check if the new position is within bounds and is a wall
            if (newX in 1 until maze.size - 1 && newY in 1 until maze[0].size - 1 && maze[newX][newY] == WALL) {
                // Check if surrounding cells are walls
                val surroundingWalls = countSurroundingWalls(maze, newX, newY)
                if (surroundingWalls >= 3) {
                    maze[newX][newY] = EMPTY
                    generateMazeRecursive(maze, newX, newY)
                }
            }
        }
    }

    private fun countSurroundingWalls(maze: Array<IntArray>, x: Int, y: Int): Int {
        var count = 0
        if (maze[x - 1][y] == WALL) count++ // left
        if (maze[x + 1][y] == WALL) count++ // right
        if (maze[x][y - 1] == WALL) count++ // up
        if (maze[x][y + 1] == WALL) count++ // down
        return count
    }

    private fun calculateScore(level: Int, collisions: Int): Int {
        val rawScore = level / 2 - collisions
        return if (rawScore < 0) 0 else rawScore
    }

    private fun showCompletionDialog(score: Int) {
        // Save score to Shared Preferences
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("maze_points", score)
        editor.apply()

        // Show completion dialog
        AlertDialog.Builder(this).apply {
            setTitle("Game Over")
            setMessage("Your score is $score")
            setPositiveButton("OK") { _, _ ->
                // Proceed to the next game
                val intent = Intent(this@Maze_MainActivity, WhacAPirateMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            setCancelable(false)
            show()
        }
    }

    companion object {
        private const val WALL = 1
        private const val EMPTY = 0
    }
}