package com.example.znajdzroznicewalimy



import android.os.Bundle
import android.widget.Button // Poprawne importowanie klasy Button
import androidx.appcompat.app.AppCompatActivity
import com.example.znajdzroznicewalimy.R
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startGameButton: Button = findViewById(R.id.startGameButton)
        startGameButton.setOnClickListener {
            val intent = Intent(this, GraRzeczonaActivity::class.java)
            startActivity(intent)
        }

//        val chooseDifficultyButton: Button = findViewById(R.id.chooseDifficultyButton)
//        chooseDifficultyButton.setOnClickListener {
//            // TODO: W przyszłosci zmiana poziomu trudnosci
//        }
    }
}
