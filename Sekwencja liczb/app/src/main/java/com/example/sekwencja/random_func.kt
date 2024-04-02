package com.example.sekwencja

import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.random.Random

class random_func {
    private var storedNumber = 0

    fun mnozeniePrzezLosowaWartosc(liczba: Int): Int {
        val losowaWartosc = if (storedNumber == 0) {
            Random.nextInt(1, 11)
        } else {
            storedNumber
        }
        storedNumber = losowaWartosc
        return liczba * losowaWartosc
    }

    fun potegowaniePrzezLosowaPotega(liczba: Int): Int {
        val losowaWartosc = if (storedNumber == 0) {
            Random.nextInt(1, 2)
        } else {
            storedNumber
        }
        storedNumber = losowaWartosc
        return liczba.toDouble().pow(losowaWartosc).toInt()
    }

    fun dodawanieLosowejWartosci(liczba: Int): Int {
        val losowaWartosc = if (storedNumber == 0) {
            Random.nextInt(1, 100)
        } else {
            storedNumber
        }
        storedNumber = losowaWartosc
        return liczba + losowaWartosc
    }

    fun odejmowanieLosowejWartosci(liczba: Int): Int {
        val losowaWartosc = if (storedNumber == 0) {
            Random.nextInt(1, 100)
        } else {
            storedNumber
        }
        storedNumber = losowaWartosc
        return liczba - losowaWartosc
    }
}