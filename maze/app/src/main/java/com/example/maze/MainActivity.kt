package com.example.maze

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import java.util.*
import android.widget.Button
import java.util.concurrent.TimeUnit
import android.os.CountDownTimer


class MainActivity : Activity() {

    private lateinit var mazeView: MazeView
    private lateinit var ballView: View
    private lateinit var restartButton: Button
    private lateinit var exitButton: Button
    private lateinit var timerTextView: TextView
    private var offsetX = 0f
    private var offsetY = 0f
    private var gameOver = false
    private var level = 1
    private var isNewMazeChecked = false

    // Timer fields
    private lateinit var timer: CountDownTimer
    private var timeLeftInMillis: Long = 60000 // 1 minute in milliseconds
    private var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViewById<TextView>(R.id.scoreTextView).visibility = View.GONE


        mazeView = findViewById(R.id.mazeView)
        ballView = findViewById(R.id.ballView)
        restartButton = findViewById(R.id.restartButton)
        exitButton = findViewById(R.id.exitButton)
        timerTextView = findViewById(R.id.timerTextView)

        restartButton.visibility = View.GONE
        exitButton.visibility = View.GONE

        // Pozostała wartość czasu, jeśli została przekazana
        timeLeftInMillis = intent.getLongExtra("TIME_LEFT", 60000)

        level = intent.getIntExtra("LEVEL", 1)
        findViewById<TextView>(R.id.levelTextView).text = "Level: $level"

        generateNewMaze()

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                timerRunning = false
                gameOver = true
                findViewById<TextView>(R.id.gameOverTextView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.gameOverTextView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.scoreTextView).apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.score_message, level)
                }
                restartButton.visibility = View.VISIBLE
                exitButton.visibility = View.VISIBLE
            }
        }

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
                            checkForNewMaze(newX, newY, timeLeftInMillis) // Przekazujemy timeLeftInMillis
                        } else {

                            restartGame()


                        }
                    }
                }
            }
            true
        }

        restartButton.setOnClickListener {
            resetGame()
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }

        startTimer()
    }



    private fun disableGameInteraction() {
        mazeView.setOnTouchListener(null) // Dezaktywacja onTouchListener
        restartButton.isEnabled = false
        exitButton.isEnabled = false
    }









    private fun generateNewMaze() {
        val maze = generateMaze(8, 8)
        if (maze != null) {
            mazeView.setMaze(convertToBooleanArray(maze), ballView)
            ballView.x = 0f // Resetuj pozycję kuli
            ballView.y = 0f
            gameOver = false // Resetuj stan gry
            findViewById<TextView>(R.id.gameOverTextView).visibility = View.INVISIBLE // Ukryj tekst końca gry
        } else {
            // Jeśli nie udało się wygenerować labiryntu, próbujemy ponownie
            generateNewMaze()
        }
    }


    private fun checkForNewMaze(newX: Float, newY: Float, timeLeftInMillis: Long) {
        // Sprawdź czy kula wejdzie wejdzie w prostokat
        if (mazeView.isBallOnRedRectangle(newX, newY) && !isNewMazeChecked) {
            // Zwiększ poziom
            level++
            isNewMazeChecked = true // Ustaw flagę na true, aby uniknąć wielokrotnego sprawdzania w jednym ruchu
            // Aktualizuj etykietę na ekranie
            // Restartuj grę z przekazaniem aktualnej wartości timera
            restartActivity(level, timeLeftInMillis)
        }
    }


    private fun restartGame() {
        disableGameInteraction()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("TIME_LEFT", timeLeftInMillis)
        intent.putExtra("LEVEL", level)
        startActivity(intent)
        finish() // Zamknij bieżącą aktywność
    }


    private fun startTimer() {
        timerRunning = true
        timer.start()
    }

    private fun updateCountdownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        findViewById<TextView>(R.id.timerTextView).text = timeLeftFormatted
    }

    private fun restartActivity(level: Int, timeLeftInMillis: Long) {
        disableGameInteraction()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("TIME_LEFT", timeLeftInMillis)
        intent.putExtra("LEVEL", level)
        startActivity(intent)
        finish()

    }


    private fun isInBounds(newX: Float, newY: Float): Boolean {
        val ballLeft = newX.toInt()
        val ballTop = newY.toInt()
        val ballRight = ballLeft + ballView.width
        val ballBottom = ballTop + ballView.height

        val mazeBitmap = createBitmapFromView(mazeView)
        val ballBitmap = createBitmapFromView(ballView)

        for (x in ballLeft until ballRight) {
            for (y in ballTop until ballBottom) {
                if (mazeBitmap.getPixel(x, y) != Color.WHITE
                    && mazeBitmap.getPixel(x, y) != Color.RED // Ignoruj kolizję z prostokątem
                    && ballBitmap.getPixel(x - ballLeft, y - ballTop) != Color.TRANSPARENT) {
                    return false
                }
            }
        }
        return true
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
        // Wypełnij labirynt ścianami
        for (x in 0 until width) {
            for (y in 0 until height) {
                maze[x][y] = WALL
            }
        }

        // Wybierz losowy punkt startowy
        val random = Random()
        val startX = random.nextInt(width)
        val startY = random.nextInt(height)

        // Wygeneruj labirynt zaczynając od punktu startowego
        generateMazeRecursive(maze, startX, startY)

        // Sprawdź czy labirynt zawiera co najmniej 5 wolnych pól
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

        // Sprawdź warunki na ilość wolnych pól z jednym sąsiadem
        if (freeSpaceCount >= 5 && singleNeighborCount == 2) {
            // Jeśli spełnione, zwróć wygenerowany labirynt
            return maze
        } else {
            // W przeciwnym razie, wygeneruj nowy labirynt
            return generateMaze(width, height)
        }
    }

    private fun countSurroundingSpaces(maze: Array<IntArray>, x: Int, y: Int): Int {
        var count = 0
        if (maze[x - 1][y] == EMPTY) count++ // lewo
        if (maze[x + 1][y] == EMPTY) count++ // prawo
        if (maze[x][y - 1] == EMPTY) count++ // góra
        if (maze[x][y + 1] == EMPTY) count++ // dół
        return count
    }





    private fun generateMazeRecursive(maze: Array<IntArray>, x: Int, y: Int) {
        maze[x][y] = EMPTY

        // Wygeneruj losową permutację kierunków (lewo, prawo, góra, dół)
        val directions = mutableListOf(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN)
        directions.shuffle()

        // Sprawdź każdy kierunek
        for (direction in directions) {
            val (dx, dy) = direction.getDelta()
            val newX = x + dx * 2
            val newY = y + dy * 2

            // Sprawdź czy nowe współrzędne są w granicach labiryntu
            if (newX in 1 until maze.size - 1 && newY in 1 until maze[0].size - 1 && maze[newX][newY] == WALL) {
                maze[x + dx][y + dy] = EMPTY
                generateMazeRecursive(maze, newX, newY)
            }
        }
    }

    private fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private enum class Direction(val dx: Int, val dy: Int) {
        LEFT(-1, 0),
        RIGHT(1, 0),
        UP(0, -1),
        DOWN(0, 1);

        fun getDelta() = Pair(dx, dy)
    }

    companion object {
        const val WALL = 1
        const val EMPTY = 0
    }

    private fun resetGame() {
        // Zatrzymaj timer
        timer.cancel()
        // Resetuj wartości
        level = 1
        timeLeftInMillis = 60000
        // Odśwież etykietę poziomu
        findViewById<TextView>(R.id.levelTextView).text = "Level: $level"
        // Ukryj TextView z wynikiem
        findViewById<TextView>(R.id.scoreTextView).visibility = View.INVISIBLE
        // Rozpocznij nową grę
        restartActivity(level, timeLeftInMillis)
    }

    }