package com.example.sekwencja

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class wrong : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong)

        // Odbieranie przekazanych wartości z obiektu 'Intent'
        val button11Value = intent.getStringExtra("button11Value")
        val button12Value = intent.getStringExtra("button12Value")
        val button13Value = intent.getStringExtra("button13Value")
        val button14Value = intent.getStringExtra("button14Value")
        val btn1Text = intent.getStringExtra("btn1Text")
        val btn2Text = intent.getStringExtra("btn2Text")
        val btn3Text = intent.getStringExtra("btn3Text")

        // Ustawianie tekstu na przyciskach
        val button11 = findViewById<Button>(R.id.button11)
        button11.text = button11Value

        val button12 = findViewById<Button>(R.id.button12)
        button12.text = button12Value

        val button13 = findViewById<Button>(R.id.button13)
        button13.text = button13Value

        val button14 = findViewById<Button>(R.id.button14)
        button14.text = button14Value

        val btn1 = findViewById<Button>(R.id.button1)
        btn1.text = btn1Text

        val btn2 = findViewById<Button>(R.id.button2)
        btn2.text = btn2Text

        val btn3 = findViewById<Button>(R.id.button3)
        btn3.text = btn3Text

        // Odczytanie przekazanej wartości bufor4 z intentu
        val correctAnswer = intent.getStringExtra("bufor4Value")

        val textViewCorrectAnswer = findViewById<TextView>(R.id.textView2)

        textViewCorrectAnswer.text = "Correct Answer: $correctAnswer"

        val nextButton = findViewById<Button>(R.id.next)
        nextButton.setOnClickListener{
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }

    }

}