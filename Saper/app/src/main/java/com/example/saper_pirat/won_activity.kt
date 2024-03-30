package com.example.saper_pirat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class won_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_won)

        val timeTextView = findViewById<TextView>(R.id.your_time)
        val time = intent.getStringExtra("TIME")
        timeTextView.text = "Your time: $time"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mainButton = findViewById<Button>(R.id.main_menu)
        mainButton.setOnClickListener{
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }

        val resetButton = findViewById<Button>(R.id.try_again)
        resetButton.setOnClickListener{
            val intent2 = Intent(this, minefield::class.java)
            startActivity(intent2)
        }


    }
}