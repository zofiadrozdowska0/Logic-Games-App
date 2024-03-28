package com.example.piraci_gra

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.widget.EditText
import android.text.InputType
import android.widget.Toast
import kotlinx.coroutines.awaitAll


class MainActivity : AppCompatActivity() {
    private lateinit var layout: ViewGroup
    private lateinit var statek: ImageView
    private lateinit var luffyCountView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var luffyCount = 0
    private var gameStarted = false // Dodano zmienną kontrolującą rozpoczęcie gry
    private val runnable = object : Runnable {
        override fun run() {
            if (!gameStarted) return // Zatrzymaj tworzenie Luffych, jeśli gra się zakończyła
            if (Random.nextBoolean() && luffyCount > 0) {
                createAndAnimateLuffyExiting()
            } else {
                createAndAnimateLuffyEntering()
            }
            handler.postDelayed(this, Random.nextLong(500, 2000))
        }
    }

    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.activity_main)
        statek = findViewById(R.id.statek)
        luffyCountView = findViewById(R.id.luffy_counter)
        startButton = findViewById(R.id.start_button)

        startButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        gameStarted = true
        handler.post(runnable)
        startButton.isEnabled = false // Wyłącz przycisk startu po rozpoczęciu

        Handler(Looper.getMainLooper()).postDelayed({
            gameStarted = false // Zatrzymaj tworzenie i animację Luffych
            handler.removeCallbacks(runnable) // Usuń wszystkie oczekujące runnable
            Handler(Looper.getMainLooper()).postDelayed({
                // Kod, który chcesz wykonać z opóźnieniem.
                askUserForLuffyCount()
            }, 4000) // Opóźnienie w milisekundach, np. 2000 ms = 2 sekundy.

        }, 20000) // 20 sekund opóźnienia
    }

    private fun askUserForLuffyCount() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Ile Luffych?")
        alertDialog.setMessage("Podaj liczbę Luffych na statku:")
        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { dialog, which ->
            val userGuess = input.text.toString().toIntOrNull()
            if (userGuess == null) {
                Toast.makeText(this, "Proszę podać liczbę.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val message = if (userGuess == luffyCount) {
                "Gratulacje! Poprawna liczba Luffych."
            } else {
                "Niepoprawna liczba. Było $luffyCount Luffych."
            }

            // Wyświetlenie komunikatu o poprawności w nowym AlertDialog
            AlertDialog.Builder(this).setTitle("Wynik")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }

        alertDialog.setNegativeButton("Anuluj") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }



    private fun createAndAnimateLuffyEntering() {
        val newLuffy = ImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(100, 125) // Rozmiar Luffiego
            setImageResource(R.drawable.luffy)  // Grafika Luffiego
            // Początkowa pozycja Luffiego (pojawienie się w zielonym obszarze)
            x = -20f  // Wartość ujemna, aby Luffy zaczął poza ekranem po lewej stronie
            y = statek.y + 660f // Wysokość dostosowana do poziomu pokładu statku
            layout.addView(this)
        }

        // Przesunięcie Luffiego do wejścia na statek
        TranslateAnimation(0f, statek.x, 0f, -60f).apply {
            duration = 3000
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    newLuffy.visibility = ImageView.GONE
                    layout.removeView(newLuffy)
                    luffyCount++
                    runOnUiThread { updateLuffyCountDisplay() }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            newLuffy.startAnimation(this)
        }
    }


    private fun createAndAnimateLuffyExiting() {
        val newLuffy = ImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(100, 125)
            setImageResource(R.drawable.luffy)
            // Początkowa pozycja Luffiego na statek (pojawienie się w czerwonym obszarze)
            x = statek.x + 470f
            y = statek.y + 600f
            layout.addView(this)
        }

        // Przesunięcie Luffiego z statku poza ekran (w prawo)
        TranslateAnimation(0f, layout.width.toFloat() - newLuffy.x + 100f, 0f, 50f).apply {
            duration = 3000
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    luffyCount--
                    runOnUiThread { updateLuffyCountDisplay() }
                }
                override fun onAnimationEnd(animation: Animation?) {
                    newLuffy.visibility = ImageView.GONE
                    layout.removeView(newLuffy)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            newLuffy.startAnimation(this)
        }
    }



    private fun updateLuffyCountDisplay() {
        luffyCountView.text = "Luffies: $luffyCount"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
