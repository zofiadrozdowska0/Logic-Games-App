package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class saper_MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saper_activity_main)

        val startGameButton = findViewById<Button>(R.id.start_game)
        startGameButton.setOnClickListener{
            val intent1 = Intent(this, saper_minefield::class.java)
            startActivity(intent1)
        }

    }



}