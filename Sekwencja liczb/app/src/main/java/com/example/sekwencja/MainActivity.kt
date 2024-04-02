package com.example.sekwencja

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var button11: Button
    private lateinit var button12: Button
    private lateinit var button13: Button
    private lateinit var button14: Button

    private val randomFunction = random_func()
    private var bufor = 0
    private var bufor2 = 0
    private var bufor3 = 0
    private var bufor4 = 0
    private lateinit var selectedFunction: ((Int) -> Int)
    private var selectedNumber = 0
    private lateinit var chronometer: Chronometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chronometer = findViewById(R.id.timer)
        chronometer.start()

        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)
        button11 = findViewById(R.id.button11)
        button12 = findViewById(R.id.button12)
        button13 = findViewById(R.id.button13)
        button14 = findViewById(R.id.button14)

        // Losuj funkcję
        val functionAndNumber = losowaFunkcjaINumer()

        // Przypisz wylosowaną funkcję i liczbę
        selectedFunction = functionAndNumber.first
        selectedNumber = functionAndNumber.second

        // Oblicz bufor, bufor2 i bufor3
        bufor = losowaFunkcjaINumer().second
        bufor2 = selectedFunction(bufor)
        bufor3 = selectedFunction(bufor2)
        bufor4 = selectedFunction(bufor3)

        // Ustaw tekst na button1 jako niemodyfikowana wartość bufor
        btn1.text = bufor.toString()
        // Ustaw tekst na button2 jako wartość bufor2
        btn2.text = bufor2.toString()

        btn3.text = bufor3.toString()


        // Wylosuj indeks przycisku z poprawną odpowiedzi
        val randomButtonId = resources.getIdentifier("button1" + (Random.nextInt(1, 5)), "id", packageName)
        val randomButton = findViewById<Button>(randomButtonId)
        randomButton.text = bufor4.toString()

        // Definicja mapy do przechowywania wartości przycisków
        val buttonValuesMap = mutableMapOf<String, String>()

        // Pętla iterująca po identyfikatorach przycisków
        val allButtonIds = listOf("button11", "button12", "button13", "button14")
        for (buttonId in allButtonIds) {
            val button = findViewById<Button>(resources.getIdentifier(buttonId, "id", packageName))
            // Jeśli to jest randomButton, to przypisz jego wartość do odpowiedniego klucza w mapie
            if (button == randomButton) {
                buttonValuesMap[buttonId] = bufor4.toString()
            } else {
                // Losowanie wartości i zapisanie jej do mapy dla innych przycisków
                val randomValue = Random.nextInt(-100, 100).toString()
                button.text = randomValue
                buttonValuesMap[buttonId] = randomValue

                // Ustawienie słuchacza kliknięcia na przycisku
                button.setOnClickListener {
                    // Tworzenie intencji i uruchomienie nowej aktywności 'wrong'
                    val intent2 = Intent(this, wrong::class.java)
                    intent2.putExtra("TIME", chronometer.text.toString())
                    chronometer.stop() // Zatrzymaj chronometr
                    intent2.putExtra("button11Value", buttonValuesMap["button11"])
                    intent2.putExtra("button12Value", buttonValuesMap["button12"])
                    intent2.putExtra("button13Value", buttonValuesMap["button13"])
                    intent2.putExtra("button14Value", buttonValuesMap["button14"])
                    intent2.putExtra("btn1Text", btn1.text.toString())
                    intent2.putExtra("btn2Text", btn2.text.toString())
                    intent2.putExtra("btn3Text", btn3.text.toString())
                    intent2.putExtra("bufor4Value", bufor4.toString())
                    startActivity(intent2)
                }
            }
        }

        // Obsługa kliknięcia przycisku z poprawną odpowiedzią
        randomButton.setOnClickListener {
            // Tworzenie intencji i uruchomienie nowej aktywności 'correct'
            val intent1 = Intent(this, correct::class.java)
            intent1.putExtra("TIME", chronometer.text.toString())
            chronometer.stop() // Zatrzymaj chronometr
            intent1.putExtra("button11Value", buttonValuesMap["button11"])
            intent1.putExtra("button12Value", buttonValuesMap["button12"])
            intent1.putExtra("button13Value", buttonValuesMap["button13"])
            intent1.putExtra("button14Value", buttonValuesMap["button14"])
            intent1.putExtra("btn1Text", btn1.text.toString())
            intent1.putExtra("btn2Text", btn2.text.toString())
            intent1.putExtra("btn3Text", btn3.text.toString())
            startActivity(intent1)
        }
    }

    // Funkcja losująca losową funkcję i liczbę
    private fun losowaFunkcjaINumer(): Pair<(Int) -> Int, Int> {
        val losowaFunkcja = Random.nextInt(1, 5) // Losowa liczba od 1 do 4 (liczba funkcji w klasie random_func)
        val losowaLiczba = Random.nextInt(1, 11) // Losowa liczba od 1 do 10
        return when (losowaFunkcja) {
            1 -> Pair(randomFunction::mnozeniePrzezLosowaWartosc, losowaLiczba)
            2 -> Pair(randomFunction::potegowaniePrzezLosowaPotega, losowaLiczba)
            3 -> Pair(randomFunction::dodawanieLosowejWartosci, losowaLiczba)
            4 -> Pair(randomFunction::odejmowanieLosowejWartosci, losowaLiczba)
            else -> throw IllegalArgumentException("Nieprawidłowy numer funkcji")
        }
    }
}
