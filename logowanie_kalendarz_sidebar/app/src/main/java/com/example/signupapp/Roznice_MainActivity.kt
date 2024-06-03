package com.example.signupapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class Roznice_MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_GRA_RZECZONA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roznice_activity_main)

        val startGameButton: Button = findViewById(R.id.startGameButton)
        startGameButton.setOnClickListener {
            val intent = Intent(this, GraRzeczonaActivity::class.java)
            startActivityForResult(intent, REQUEST_GRA_RZECZONA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GRA_RZECZONA && resultCode == RESULT_OK) {
            setResult(RESULT_OK) // Set the result to OK to pass back to wybor_gry
            finish() // End this activity to return to wybor_gry
        }
    }
}
