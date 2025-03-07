package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class sekwencja_correct : AppCompatActivity() {
    private fun savePointsToSharedPreferences(key: String, points: Int) {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, points)
        editor.apply()
    }

    private fun saveTotalPointsToDatabase() {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val roznicePoints = sharedPreferences.getInt("pierwsza", 0)
        val ufoludkiPoints = sharedPreferences.getInt("druga", 0)
        val klockiPoints = sharedPreferences.getInt("trzecia", 0)
        val totalPoints = roznicePoints + ufoludkiPoints + klockiPoints

        // Retrieve the username from SharedPreferences
        val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "Unknown User")

        val db = FirebaseFirestore.getInstance()
        val pointsData = hashMapOf(
            "username" to username,
            "logic_points" to totalPoints,
            "date" to com.google.firebase.Timestamp.now()
        )

        db.collection("points")
            .add(pointsData)
            .addOnSuccessListener {
                println("Points successfully written!")
            }
            .addOnFailureListener { e ->
                println("Error writing document: $e")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sekwencja_activity_correct)

        val timeTextView = findViewById<TextView>(R.id.text_time)
        val time = intent.getStringExtra("TIME")
        timeTextView.text = "Your time: $time"

        val correctCount = intent.getIntExtra("CORRECT_COUNT", 0)
        val wrongCount = intent.getIntExtra("WRONG_COUNT", 0)

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

        if (correctCount + wrongCount >= 10) {
            showAllGamesCompletedDialog()
            Handler().postDelayed({
                savePointsToSharedPreferences("trzecia", correctCount)
                saveTotalPointsToDatabase()
                val intent = Intent(this, wybor_gry::class.java)
                startActivity(intent)
                finish()
            }, 2000) // Show the "All games completed!" dialog after 2 seconds
        }

        val nextButton = findViewById<Button>(R.id.nextB)
        nextButton.setOnClickListener {
            val intent1 = Intent(this, sekwencja_MainActivity::class.java)
            intent1.putExtra("CORRECT_COUNT", correctCount)
            intent1.putExtra("WRONG_COUNT", wrongCount)
            startActivity(intent1)
        }
    }

    private fun showAllGamesCompletedDialog() {
        saveTotalPointsToDatabase()
        AlertDialog.Builder(this)
            .setTitle("All games completed!")
            .setMessage("Congratulations! You have completed all games.")
            .setPositiveButton("OK") { _, _ ->

                finish()
            }
            .setCancelable(false)
            .show()
    }
}
