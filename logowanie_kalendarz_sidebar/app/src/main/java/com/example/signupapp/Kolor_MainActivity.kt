package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog

class Kolor_MainActivity : ComponentActivity() {
    public var score = 0
    private var partscore = 0
    private var buttonClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kolor_main)

        val textpoints: TextView = findViewById(R.id.textPoints)
        textpoints.text = "Points: 0"
        val texttime: TextView = findViewById(R.id.textTime)
        val mainbut: Button = findViewById(R.id.mainButton)
        var gra = true
        var previousPair: Pair<String, String>? = null
        val listaKolorow: List<Pair<String, String>> = listOf(
            Pair("red", "#FF0000"),
            Pair("red", "#FF0000"),
            Pair("red", "#FF0000"),
            Pair("red", "#0000FF"),
            Pair("red", "#00FF00"),
            Pair("red", "#FFFF00"),
            Pair("red", "#FFC0CB"),
            Pair("red", "#FFFFFF"),
            Pair("red", "#000000"),
            Pair("blue", "#0000FF"),
            Pair("blue", "#0000FF"),
            Pair("blue", "#0000FF"),
            Pair("blue", "#FF0000"),
            Pair("blue", "#00FF00"),
            Pair("blue", "#FFFF00"),
            Pair("blue", "#FFC0CB"),
            Pair("blue", "#FFFFFF"),
            Pair("blue", "#000000"),
            Pair("green", "#00FF00"),
            Pair("green", "#00FF00"),
            Pair("green", "#00FF00"),
            Pair("green", "#FF0000"),
            Pair("green", "#0000FF"),
            Pair("green", "#FFFF00"),
            Pair("green", "#FFC0CB"),
            Pair("green", "#FFFFFF"),
            Pair("green", "#000000"),
            Pair("yellow", "#FFFF00"),
            Pair("yellow", "#FFFF00"),
            Pair("yellow", "#FFFF00"),
            Pair("yellow", "#FF0000"),
            Pair("yellow", "#0000FF"),
            Pair("yellow", "#00FF00"),
            Pair("yellow", "#FFC0CB"),
            Pair("yellow", "#FFFFFF"),
            Pair("yellow", "#000000"),
            Pair("pink", "#FFC0CB"),
            Pair("pink", "#FFC0CB"),
            Pair("pink", "#FFC0CB"),
            Pair("pink", "#FF0000"),
            Pair("pink", "#0000FF"),
            Pair("pink", "#00FF00"),
            Pair("pink", "#FFFF00"),
            Pair("pink", "#FFFFFF"),
            Pair("pink", "#000000"),
            Pair("white", "#FFFFFF"),
            Pair("white", "#FFFFFF"),
            Pair("white", "#FFFFFF"),
            Pair("white", "#FF0000"),
            Pair("white", "#0000FF"),
            Pair("white", "#00FF00"),
            Pair("white", "#FFFF00"),
            Pair("white", "#FFC0CB"),
            Pair("white", "#000000"),
            Pair("black", "#000000"),
            Pair("black", "#000000"),
            Pair("black", "#000000"),
            Pair("black", "#FF0000"),
            Pair("black", "#0000FF"),
            Pair("black", "#00FF00"),
            Pair("black", "#FFFF00"),
            Pair("black", "#FFC0CB"),
            Pair("black", "#FFFFFF")
        )

        fun updateButtonColorAndText() {
            var randomPair: Pair<String, String>
            do {
                val randomIndex = (0 until listaKolorow.size).random()
                randomPair = listaKolorow[randomIndex]
            } while (randomPair == previousPair)

            mainbut.text = randomPair.first
            mainbut.setTextColor(android.graphics.Color.parseColor(randomPair.second))
            previousPair = randomPair
            buttonClickable = true
        }

        mainbut.setOnClickListener {
            if (buttonClickable) {
                val currentTextColor = mainbut.currentTextColor
                val currentColorName = mainbut.text

                when {
                    currentTextColor == -65536 && currentColorName == "red" -> score++
                    currentTextColor == -16776961 && currentColorName == "blue" -> score++
                    currentTextColor == -16711936 && currentColorName == "green" -> score++
                    currentTextColor == -256 && currentColorName == "yellow" -> score++
                    currentTextColor == -16181 && currentColorName == "pink" -> score++
                    currentTextColor == -1 && currentColorName == "white" -> score++
                    currentTextColor == -16777216 && currentColorName == "black" -> score++
                    else -> score--
                }
                if (score == 10) {
                    showCompletionDialog(score)
                }
                buttonClickable = false // Ustawiamy flagę na false po kliknięciu
            }
        }

        val timer2 = object : CountDownTimer(61100, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                texttime.text = "Time left: $minutes:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                texttime.text = "Time's up!"
                mainbut.isEnabled = false
                gra = false
                showCompletionDialog(score)
            }
        }
        timer2.start()

        var roundcount = 0
        val timer = object : CountDownTimer(31200, 2400) {
            override fun onTick(millisUntilFinished: Long) {
                if (gra) {
                    textpoints.text = "Points: $score"
                    val currentTextColor = mainbut.currentTextColor
                    val currentColorName = mainbut.text
                    if (buttonClickable) {
                        if (!((currentTextColor == -65536 && currentColorName == "red") ||
                                    (currentTextColor == -16776961 && currentColorName == "blue") ||
                                    (currentTextColor == -16711936 && currentColorName == "green") ||
                                    (currentTextColor == -256 && currentColorName == "yellow") ||
                                    (currentTextColor == -16181 && currentColorName == "pink") ||
                                    (currentTextColor == -1 && currentColorName == "white") ||
                                    (currentTextColor == -16777216 && currentColorName == "black"))
                        ) {
                            partscore++

                            if (partscore == 3) {
                                score++
                                partscore = 0
                            }
                            if (score == 10) {
                                showCompletionDialog(score)
                            }
                            textpoints.text = "Points: $score"
                        }
                    }
                    updateButtonColorAndText()
                    roundcount++
                }
            }

            override fun onFinish() {
                val timer3 = object : CountDownTimer(19500, 1500) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (gra) {
                            textpoints.text = "Points: $score"
                            val currentTextColor = mainbut.currentTextColor
                            val currentColorName = mainbut.text
                            if (buttonClickable) {
                                if (!((currentTextColor == -65536 && currentColorName == "red") ||
                                            (currentTextColor == -16776961 && currentColorName == "blue") ||
                                            (currentTextColor == -16711936 && currentColorName == "green") ||
                                            (currentTextColor == -256 && currentColorName == "yellow") ||
                                            (currentTextColor == -16181 && currentColorName == "pink") ||
                                            (currentTextColor == -1 && currentColorName == "white") ||
                                            (currentTextColor == -16777216 && currentColorName == "black"))
                                ) {
                                    partscore++

                                    if (partscore == 3) {
                                        score++
                                        partscore = 0
                                    }
                                    if (score == 10) {
                                        showCompletionDialog(score)
                                    }
                                    textpoints.text = "Points: $score"
                                }
                            }
                            updateButtonColorAndText()
                            roundcount++
                        }
                    }

                    override fun onFinish() {
                        val timer4 = object : CountDownTimer(10400, 800) {
                            override fun onTick(millisUntilFinished: Long) {
                                if (gra) {
                                    textpoints.text = "Points: $score"
                                    val currentTextColor = mainbut.currentTextColor
                                    val currentColorName = mainbut.text
                                    if (buttonClickable) {
                                        if (!((currentTextColor == -65536 && currentColorName == "red") ||
                                                    (currentTextColor == -16776961 && currentColorName == "blue") ||
                                                    (currentTextColor == -16711936 && currentColorName == "green") ||
                                                    (currentTextColor == -256 && currentColorName == "yellow") ||
                                                    (currentTextColor == -16181 && currentColorName == "pink") ||
                                                    (currentTextColor == -1 && currentColorName == "white") ||
                                                    (currentTextColor == -16777216 && currentColorName == "black"))
                                        ) {
                                            partscore++

                                            if (partscore == 3) {
                                                score++
                                                partscore = 0
                                            }
                                            if (score == 10) {
                                                showCompletionDialog(score)
                                            }
                                            textpoints.text = "Points: $score"
                                        }
                                    }
                                    updateButtonColorAndText()
                                    roundcount++
                                }
                            }

                            override fun onFinish() {
                                showCompletionDialog(score) // Show final score dialog at the end
                            }
                        }
                        timer4.start()
                    }
                }
                timer3.start()
            }
        }
        timer.start()
    }

    private fun showCompletionDialog(finalScore: Int) {
        // Save score to Shared Preferences
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("kolor_points", finalScore)
        editor.apply()

        // Show completion dialog
        AlertDialog.Builder(this).apply {
            setTitle("Game Over")
            setMessage("Your score is $finalScore")
            setPositiveButton("OK") { _, _ ->
                // Proceed to the next game
                val intent = Intent(this@Kolor_MainActivity, Maze_MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            setCancelable(false)
            show()
        }
    }
}
