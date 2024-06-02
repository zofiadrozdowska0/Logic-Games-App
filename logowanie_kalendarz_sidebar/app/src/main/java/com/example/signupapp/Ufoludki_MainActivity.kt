package com.example.signupapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.ViewGroup
import android.widget.ImageView
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.signupapp.R

class Ufoludki_MainActivity : AppCompatActivity() {
    private lateinit var layout: ViewGroup
    private lateinit var statek: ImageView
    private val obsluga = Handler(Looper.getMainLooper())
    private var graRozpoczeta = false
    private var wartoscLicznika = 0
    private var liczbaNiebieskichPiratow = 0
    private var animacjaTrwa = false
    var listaPiratowWchodzacych = mutableListOf<Int>()
    var listaPiratowWychodzacych = mutableListOf<Int>()
    var czas_pirata_1 = 0.toLong()
    var czas_pirata_2 = 0.toLong()
    var czas_animacji = 0.toLong()
    private val zadanie_wchodzenie = object : Runnable {
        override fun run() {
            if (!graRozpoczeta) return
            val wybranyPirat = listaPiratowWchodzacych[Random.nextInt(listaPiratowWchodzacych.size)]
            stworzIAnimujPirata(wybranyPirat)
            obsluga.postDelayed(this, Random.nextLong(czas_pirata_1, czas_pirata_2))//zalezne od trybu gry
        }
    }
    private val zadanie_wychodzenie = object : Runnable {
        override fun run() {
            if (!graRozpoczeta) return
            val wybranyPirat = listaPiratowWychodzacych[Random.nextInt(listaPiratowWychodzacych.size)]
            stworzIAnimujPirataWychodzacego(wybranyPirat)
            obsluga.postDelayed(this, Random.nextLong(czas_pirata_1, czas_pirata_2)) //zalezne od trybu gry
        }
    }

    private fun wybierzPoziomTrudnosciIGraj() {
        val poziomyTrudnosci = arrayOf("Łatwy", "Średni", "Trudny")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz poziom trudności")
            .setSingleChoiceItems(poziomyTrudnosci, -1) { dialog, which ->
                ustawParametryTrudnosci(poziomyTrudnosci[which])
                dialog.dismiss()
                rozpocznijGrę()
            }
        builder.create().show()
    }

    private fun ustawParametryTrudnosci(poziom: String) {
        listaPiratowWchodzacych.clear() // Wyczyść listę przed ustawieniem nowych wartości
        listaPiratowWychodzacych.clear() // Wyczyść listę przed ustawieniem nowych wartości
        when (poziom) {
            "Łatwy" -> {
                czas_pirata_1 = 500
                czas_pirata_2 = 2000
                czas_animacji = 2000
                listaPiratowWchodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat))
                listaPiratowWychodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat))
            }
            "Średni" -> {
                czas_pirata_1 = 500
                czas_pirata_2 = 2000
                czas_animacji = 1500
                listaPiratowWchodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_czerwony_pirat, R.drawable.ufoludki_niebieski_pirat))
                listaPiratowWychodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat, R.drawable.ufoludki_niebieski_pirat))
            }
            "Trudny" -> {
                czas_pirata_1 = 300
                czas_pirata_2 = 1000
                czas_animacji = 1000
                listaPiratowWchodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_czerwony_pirat, R.drawable.ufoludki_czerwony_pirat, R.drawable.ufoludki_niebieski_pirat))
                listaPiratowWychodzacych.addAll(listOf(R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_zielony_pirat,R.drawable.ufoludki_niebieski_pirat))
            }
        }
    }



    private lateinit var przyciskStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufoludki_activity_main)

        layout = findViewById(R.id.ufoludki_activity_main)
        statek = findViewById(R.id.statek)
        przyciskStart = findViewById(R.id.start_button)

        przyciskStart.setOnClickListener {
            wybierzPoziomTrudnosciIGraj()
        }
    }

    private fun rozpocznijGrę() {
        graRozpoczeta = true
        obsluga.post(zadanie_wchodzenie)
        obsluga.post(zadanie_wychodzenie)
        przyciskStart.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            graRozpoczeta = false // Zakończ grę po 40 sekundach
            zapytajOUzyskanePunkty()
        }, 40000) // 40 sekund w milisekundach
    }

    private fun zapytajOUzyskanePunkty() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Koniec gry!")
            .setMessage("Ile piratów jest na statku?")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val odpowiedz = input.text.toString().toIntOrNull()
                sprawdzOdpowiedz(odpowiedz)
            }
            .show()
    }

    private fun sprawdzOdpowiedz(odpowiedz: Int?) {
        val wiadomosc = if (odpowiedz == wartoscLicznika) {
            "Poprawna odpowiedź!"
        } else {
            "Niepoprawna odpowiedź. Na statku jest $wartoscLicznika piratów."
        }

        AlertDialog.Builder(this)
            .setTitle("Wynik")
            .setMessage(wiadomosc)
            .setPositiveButton("OK") { _, _ ->
                zresetujGre()
            }
            .show()
    }

    private fun zresetujGre() {
        wartoscLicznika = 0
        liczbaNiebieskichPiratow = 0
        animacjaTrwa = false
        graRozpoczeta = false
        przyciskStart.isEnabled = true
        // Wyczyść wszystkie piraty, które mogłyby być jeszcze na ekranie
        layout.removeAllViews()
        // Dodaj statek z powrotem do layoutu, jeśli został usunięty
        layout.addView(statek)
        layout.addView(przyciskStart)
        // Możesz także ustawić tutaj wszelkie inne zmienne, które chcesz zresetować do stanu początkowego
    }



    private fun stworzIAnimujPirata(obrazPirata: Int) {
        if (animacjaTrwa) return // Nie rozpoczynaj nowej animacji, jeśli trwa inna
        animacjaTrwa = true
        val pirat = ImageView(this).apply {
            setImageResource(obrazPirata)
            val wysokoscPirata = statek.height /5
            val szerokoscPirata = wysokoscPirata * (drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat())
            layoutParams = ViewGroup.LayoutParams(szerokoscPirata.toInt(), wysokoscPirata)
        }

        layout.addView(pirat)
        pirat.y = statek.y+statek.height/1.5f


        val rzeczywistaSzerokoscStatku = statek.width * statek.scaleX
        val koniecX = layout.width / 2 - rzeczywistaSzerokoscStatku/2 - pirat.width

        val animacja = TranslateAnimation(
            Animation.ABSOLUTE, -pirat.layoutParams.width.toFloat(),
            Animation.ABSOLUTE, koniecX,
            Animation.ABSOLUTE, 0f,
            Animation.ABSOLUTE, 0f
        ).apply {
            duration = czas_animacji
            fillAfter = false

            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    layout.removeView(pirat) // Usunięcie widoku pirata po zakończeniu animacji
                    animacjaTrwa = false // Ustawienie flagi na false, aby umożliwić kolejne animacje
                    // Aktualizacja wartości licznika w zależności od koloru pirata
                    when (obrazPirata) {
                        R.drawable.ufoludki_zielony_pirat -> wartoscLicznika += 1
                        R.drawable.ufoludki_czerwony_pirat -> wartoscLicznika *= 2
                        R.drawable.ufoludki_niebieski_pirat -> liczbaNiebieskichPiratow += 1 // Inkrementacja
                    }
                    Log.d("LicznikPiratow", "Aktualna wartość licznika: $wartoscLicznika, Niebieskich Piratów: $liczbaNiebieskichPiratow")
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        pirat.startAnimation(animacja)
    }

    private fun stworzIAnimujPirataWychodzacego(obrazPirata: Int) {
        if (animacjaTrwa) return // Nie rozpoczynaj nowej animacji, jeśli trwa inna
        // Warunek dla zielonego pirata - może wyjść tylko jeśli licznik > 0
        if (obrazPirata == R.drawable.ufoludki_zielony_pirat && wartoscLicznika <= 0) return

        // Warunek dla niebieskiego pirata - może wyjść tylko jeśli jakiś wszedł
        if (obrazPirata == R.drawable.ufoludki_niebieski_pirat && liczbaNiebieskichPiratow <= 0) return
        animacjaTrwa = true
        val pirat = ImageView(this).apply {
            setImageResource(obrazPirata)
            val wysokoscPirata = statek.height /5
            val szerokoscPirata = wysokoscPirata * (drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat())
            layoutParams = ViewGroup.LayoutParams(szerokoscPirata.toInt(), wysokoscPirata)
        }

        layout.addView(pirat)
        //pirat.y = layout.height / 2f - (pirat.layoutParams.height / 2f) - statek.height
        pirat.y = statek.y+statek.height/1.5f

        val rzeczywistaSzerokoscStatku = statek.width * statek.scaleX
        val startX = layout.width / 2 + rzeczywistaSzerokoscStatku/4

        val animacja = TranslateAnimation(
                Animation.ABSOLUTE, startX, // Start X: obok statku, po jego prawej stronie
                Animation.ABSOLUTE, layout.width.toFloat() - pirat.x, // Koniec X: koniec ekranu
                Animation.ABSOLUTE, 0f, // Start Y: bez zmian
                Animation.ABSOLUTE, 0f  // Koniec Y: bez zmian
            ).apply {
                duration = czas_animacji
                fillAfter = false

                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        layout.removeView(pirat) // Usunięcie widoku pirata po zakończeniu animacji
                        animacjaTrwa = false // Ustawienie flagi na false, aby umożliwić kolejne animacje
                        if (obrazPirata == R.drawable.ufoludki_zielony_pirat) {
                            wartoscLicznika -= 1
                        } else if (obrazPirata == R.drawable.ufoludki_niebieski_pirat) {
                            liczbaNiebieskichPiratow -= 1 // Dekrementacja dla niebieskiego pirata
                        }
                        Log.d("LicznikPiratow", "Aktualna wartość licznika: $wartoscLicznika, Niebieskich Piratów: $liczbaNiebieskichPiratow")
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
            pirat.startAnimation(animacja)
        }


    override fun onDestroy() {
        super.onDestroy()
        obsluga.removeCallbacks(zadanie_wchodzenie)
        obsluga.removeCallbacks(zadanie_wychodzenie)
    }
}
