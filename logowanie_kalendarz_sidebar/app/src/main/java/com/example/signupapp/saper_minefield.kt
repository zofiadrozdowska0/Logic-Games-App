package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.random.Random

class saper_minefield : AppCompatActivity() {
    private var rows=12
    private var columns=8
    private var mines=16
    private var flaggedMines = 0  // Licznik oflagowanych min
    private val imageResources = listOf(
        R.drawable.saper_bomb,
        R.drawable.saper_flag,
    )
    private lateinit var chronometer: Chronometer
    private var isFirstMove = true

    // Indeks aktualnego obrazka w liście
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saper_activity_minefield)

        // Start the timer
        chronometer = findViewById(R.id.timer)
        chronometer.start()

        val fieldLayout = findViewById<LinearLayout>(R.id.field) // Znajdujemy LinearLayout field

        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % imageResources.size
            val nextImageResource = imageResources[currentIndex]
            imageButton.setImageResource(nextImageResource)
        }
        val resetButton = findViewById<Button>(R.id.button_reset)
        resetButton.setOnClickListener {
            isFirstMove = true
            val intent1 = Intent(this, saper_minefield::class.java)
            startActivity(intent1)
        }

        // Ustawienie początkowej wartości TextView how_many_left na liczbę min
        val minesLeftTextView = findViewById<TextView>(R.id.how_many_left)
        minesLeftTextView.text = mines.toString()

        val mineboard = Array(rows) { Array(columns) { saper_MineCell(this) } }

        drawMineboard(fieldLayout, mineboard)
    }

    private fun drawMineboard(fieldLayout: LinearLayout, mineboard: Array<Array<saper_MineCell>>) {
        var cell_id = 1

        for (i in 0 until rows) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
            ).apply {
                weight = 1.0F
            }

            for (j in 0 until columns) {
                val button = mineboard[i][j]
                button.id = cell_id
                cell_id++

                button.layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    weight = 1.0F
                }

                button.setBackgroundResource(R.drawable.saper_orange_square)

                button.setOnClickListener {
                    if (isFirstMove) {
                        isFirstMove = false
                        placeMines(mineboard, rows, columns, mines, i, j)
                    }
                    if (currentIndex == 1 && button.background.constantState?.equals(ContextCompat.getDrawable(this, R.drawable.saper_orange_square)?.constantState) == true) { // Sprawdzamy, czy aktualnie wybrany obrazek to flaga
                        // flag the field
                        button.setBackgroundResource(R.drawable.saper_flag_square)
                        button.setFlaggedStatus(true)
                        flaggedMines++  // Zwiększ licznik oflagowanych min
                        updateFlaggedMinesCount(mineboard)  // Aktualizuj TextView
                    } else if (currentIndex == 1 && button.background.constantState?.equals(ContextCompat.getDrawable(this, R.drawable.saper_flag_square)?.constantState) == true) {
                        // remove flag
                        button.setBackgroundResource(R.drawable.saper_orange_square)
                        button.setFlaggedStatus(false)
                        flaggedMines--  // Zmniejsz licznik oflagowanych min
                        updateFlaggedMinesCount(mineboard)  // Aktualizuj TextView
                    } else if (currentIndex == 0 && button.background.constantState?.equals(ContextCompat.getDrawable(this, R.drawable.saper_flag_square)?.constantState) == true) {
                        // do nothing
                        button.setBackgroundResource(R.drawable.saper_flag_square)
                    } else { // Jeśli aktualnie wybrany obrazek to nie flaga
                        if (button.hasBomb) {
                            // Odslon wszystkie pola z bombami
                            revealAllBombFields(mineboard)

                            // Zaczekaj sekudne przed uruchomieniem lost_activity
                            Handler().postDelayed({
                                val intent2 = Intent(this, saper_lost_activity::class.java)
                                startActivity(intent2)
                            }, 1000) // Czas w milisekundach
                        } else {
                            val bombCount = countAdjacentBombs(mineboard, i, j)
                            val resourceId = getBombCountDrawableResource(bombCount)
                            button.setBackgroundResource(resourceId)
                            button.setRevealed() // Oznacz pole jako odkryte
                            if (bombCount == 0) {
                                revealAdjacentEmptyCells(mineboard, i, j)
                            }
                        }
                    }
                }

                linearLayout.addView(button)
            }
            fieldLayout.addView(linearLayout)
        }
    }

    private fun placeMines(mineboard: Array<Array<saper_MineCell>>, rows: Int, columns: Int, mines: Int, firstMoveRow: Int, firstMoveColumn: Int) {
        val random = Random(System.currentTimeMillis())

        var minesPlaced = 0
        while (minesPlaced < mines) {
            val row = random.nextInt(rows)
            val column = random.nextInt(columns)

            if (!mineboard[row][column].hasBomb && (row != firstMoveRow || column != firstMoveColumn)) {
                mineboard[row][column].hasBomb = true
                minesPlaced++
            }
        }
    }

    private fun countAdjacentBombs(mineboard: Array<Array<saper_MineCell>>, row: Int, column: Int): Int {
        var count = 0
        for (i in (row - 1)..(row + 1)) {
            for (j in (column - 1)..(column + 1)) {
                if (i in 0 until rows && j in 0 until columns && !(i == row && j == column)) {
                    if (mineboard[i][j].hasBomb) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun getBombCountDrawableResource(bombCount: Int): Int {
        return when (bombCount) {
            0 -> R.drawable.saper_no_bombs
            1 -> R.drawable.saper_one_bombs
            2 -> R.drawable.saper_two_bombs
            3 -> R.drawable.saper_three_bombs
            4 -> R.drawable.saper_four_bombs
            5 -> R.drawable.saper_five_bombs
            6 -> R.drawable.saper_six_bombs
            7 -> R.drawable.saper_seven_bombs
            8 -> R.drawable.saper_eight_bombs
            else -> throw IllegalArgumentException("Invalid bomb count: $bombCount")
        }
    }

    private fun revealAllBombFields(mineboard: Array<Array<saper_MineCell>>) {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val button = mineboard[i][j]
                if (button.hasBomb) {
                    button.setBackgroundResource(R.drawable.saper_yes_bombs)
                }
            }
        }
    }

    private fun revealAdjacentEmptyCells(mineboard: Array<Array<saper_MineCell>>, row: Int, column: Int) {
        for (i in (row - 1)..(row + 1)) {
            for (j in (column - 1)..(column + 1)) {
                if (i in 0 until rows && j in 0 until columns && !(i == row && j == column)) {
                    val button = mineboard[i][j]
                    if (!button.hasBomb && button.background.constantState?.equals(
                            ContextCompat.getDrawable(this, R.drawable.saper_orange_square)?.constantState
                        ) == true
                    ) {
                        val bombCount = countAdjacentBombs(mineboard, i, j)
                        val resourceId = getBombCountDrawableResource(bombCount)
                        button.setBackgroundResource(resourceId)
                        if (bombCount == 0) {
                            revealAdjacentEmptyCells(mineboard, i, j)
                        }
                    }
                }
            }
        }
    }

    private fun updateFlaggedMinesCount(mineboard: Array<Array<saper_MineCell>>) {
        val minesLeftTextView = findViewById<TextView>(R.id.how_many_left)
        minesLeftTextView.text = (mines - flaggedMines).toString()

        // Sprawdź, czy wszystkie pola z bombami są oflagowane i wszystkie pozostałe pola są odkryte
        if (flaggedMines == mines && checkAllCellsRevealedWithoutBomb(mineboard)) {
            // Jeśli tak, przejdź do won_activity
            val intent = Intent(this, saper_won_activity::class.java)
            intent.putExtra("TIME", chronometer.text.toString())
            chronometer.stop() // Zatrzymaj chronometr
            startActivity(intent)
        }
    }

    private fun checkAllCellsRevealedWithoutBomb(mineboard: Array<Array<saper_MineCell>>): Boolean {
        var allBombsFlagged = true
        var allNonBombCellsRevealed = true

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val button = mineboard[i][j]
                if (!button.isRevealed && !button.hasBomb && !button.isFlagged) {
                    allNonBombCellsRevealed = false // Jeśli istnieje nieodkryte pole bez bomby i bez flagi, ustaw flagę na false
                }
                if (button.hasBomb && !button.isFlagged) {
                    allBombsFlagged = false // Jeśli istnieje pole z bombą nieoznaczone flagą, ustaw flagę na false
                }
            }
        }
        return allBombsFlagged || allNonBombCellsRevealed || flaggedMines > mines
    }
}
