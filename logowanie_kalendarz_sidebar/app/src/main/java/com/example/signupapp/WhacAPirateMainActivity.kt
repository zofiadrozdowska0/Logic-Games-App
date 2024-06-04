package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

class WhacAPirateMainActivity : AppCompatActivity() {
    private var score = 0
    private var appearanceTimer: Timer? = null
    private var level = 1
    private var lives = 3 // Poziom trudności

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.whacapiratelayout)

        // Inicjalizacja wszystkich przycisków obrazków
        val imageButtons = listOf<ImageButton>(
            findViewById(R.id.imageButton1),
            findViewById(R.id.imageButton2),
            findViewById(R.id.imageButton3),
            findViewById(R.id.imageButton4),
            findViewById(R.id.imageButton5),
            findViewById(R.id.imageButton6),
            findViewById(R.id.imageButton7),
            findViewById(R.id.imageButton8),
            findViewById(R.id.imageButton9)
        )

        // Inicjalizacja TextView dla wyniku
        val scoreTextView = findViewById<TextView>(R.id.textView5)
        // Inicjalizacja TextView dla komunikatu "Game Over"
        val gameOverTextView = TextView(this).apply {
            text = "Game Over! Score: $score"
            textSize = 34f
            gravity = Gravity.CENTER
            visibility = View.INVISIBLE
        }
        (findViewById<ConstraintLayout>(R.id.Glowny)).addView(gameOverTextView)

        // Ukrycie wszystkich obrazków na początku
        imageButtons.forEach { it.visibility = ImageButton.INVISIBLE }

        // Funkcja do aktualizacji widoczności obrazków serc
        fun updateLivesUI() {
            val heart1 = findViewById<ImageView>(R.id.serce1)
            val heart2 = findViewById<ImageView>(R.id.serce2)
            val heart3 = findViewById<ImageView>(R.id.serce3)
            when (lives) {
                3 -> {
                    heart1.visibility = ImageView.VISIBLE
                    heart2.visibility = ImageView.VISIBLE
                    heart3.visibility = ImageView.VISIBLE
                }
                2 -> {
                    heart1.visibility = ImageView.INVISIBLE
                    heart2.visibility = ImageView.VISIBLE
                    heart3.visibility = ImageView.VISIBLE
                }
                1 -> {
                    heart1.visibility = ImageView.INVISIBLE
                    heart2.visibility = ImageView.INVISIBLE
                    heart3.visibility = ImageView.VISIBLE
                }
                0 -> {
                    heart1.visibility = ImageView.INVISIBLE
                    heart2.visibility = ImageView.INVISIBLE
                    heart3.visibility = ImageView.INVISIBLE
                }
            }
        }

        fun gameOver(message: String = "Game Over! Score: $score") {
            runOnUiThread {
                findViewById<TextView>(R.id.textView4).text = message
                Handler().postDelayed({
                    val intent1 = Intent(applicationContext, wybor_gry::class.java)
                    startActivity(intent1)
                }, 1000)
            }
            appearanceTimer?.cancel()
        }

        // Funkcja do tracenia życia
        fun loseLife() {
            lives-- // Zmniejsz liczbę żyć
            updateLivesUI() // Aktualizuj widoczność obrazków serc
            if (lives == 0) {
                gameOver() // Sprawdź, czy gracz stracił wszystkie życia
            }
        }

        // Funkcja do losowego pojawiania się obrazków piratów w losowym czasie
        fun randomAppearance() {
            val randomIndex = Random.nextInt(imageButtons.size)
            val imageButton = imageButtons[randomIndex]
            val minDelayMillis = 500L // Minimalne opóźnienie w milisekundach
            val maxDelayMillis = 2000L - (level * 100) // Maksymalne opóźnienie, zmniejszane wraz z poziomem trudności
            val delayMillis = Random.nextLong(minDelayMillis, maxDelayMillis)
            imageButton.postDelayed({
                imageButton.startAnimation(AnimationUtils.loadAnimation(this@WhacAPirateMainActivity, R.anim.whacapirateanimation))
                imageButton.visibility = ImageButton.VISIBLE // Ustaw widoczność na widoczne po zakończeniu animacji
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            if (imageButton.visibility == ImageButton.VISIBLE) {
                                imageButton.visibility = ImageButton.INVISIBLE
                                loseLife() // Ukryj obrazek, jeśli nie został kliknięty
                            }
                        }
                    }
                }, 1000) // Ustaw czas, po którym obrazek zniknie (np. 3 sekundy)
            }, delayMillis)
        }

        // Rozpocznij losowe pojawianie się obrazków piratów
        val appearanceTimerTask = object : TimerTask() {
            override fun run() {
                randomAppearance()
            }
        }
        appearanceTimer = Timer()
        appearanceTimer?.schedule(appearanceTimerTask, 0, 2000) // Uruchamia co 2 sekundy

        // Ustawienie obsługi zdarzenia kliknięcia dla każdego przycisku obrazka
        imageButtons.forEach { imageButton ->
            imageButton.setOnClickListener {
                if (imageButton.visibility == ImageButton.VISIBLE) {
                    score++
                    scoreTextView.text = "Score: $score"
                    imageButton.visibility = ImageButton.INVISIBLE
                    if (score == 10) {
                        gameOver("Max Score! Score: $score")
                    } else if (score % 3 == 0) { // Zwiększ poziom trudności co 3 punkty
                        level++
                    }
                }
            }
        }
    }
}
