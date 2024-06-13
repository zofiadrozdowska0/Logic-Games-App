package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.signupapp.signup
import com.example.signupapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnsignup: Button
    private lateinit var btnlogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnsignup = findViewById(R.id.button)
        btnlogin = findViewById(R.id.button2)

        btnsignup.setOnClickListener{
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }
        btnlogin.setOnClickListener{
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }
}
