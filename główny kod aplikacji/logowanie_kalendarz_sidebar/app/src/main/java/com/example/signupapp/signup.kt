package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.editTextTextEmail)
        val usernameEditText = findViewById<EditText>(R.id.editTextText3)
        val passwordEditText = findViewById<EditText>(R.id.editTextText4)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextText5)
        val signupButton = findViewById<Button>(R.id.button3)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    // Sprawdź, czy nazwa użytkownika już istnieje
                    firestore.collection("users").whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                // Nie znaleziono istniejącej nazwy użytkownika, można tworzyć nowe konto
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = auth.currentUser
                                            val uid = user?.uid

                                            val userData = hashMapOf(
                                                "username" to username,
                                                "email" to email
                                            )

                                            if (uid != null) {
                                                firestore.collection("users").document(uid).set(userData)
                                                    .addOnSuccessListener {
                                                        // Przejdź do `activity_success`
                                                        val succesIntent = Intent(this, succes::class.java)
                                                        startActivity(succesIntent)
                                                        finish()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this, "Błąd zapisu danych: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        } else {
                                            Toast.makeText(this, "Rejestracja nieudana: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                // Nazwa użytkownika już istnieje
                                Toast.makeText(this, "Nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Błąd podczas sprawdzania nazwy użytkownika: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Hasła nie pasują!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Wprowadź wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun addRandomPointsInFuture(uid: String?) {
        if (uid != null) {
            val calendar = Calendar.getInstance()

            for (i in 0 until 5) {
                val currentDate = calendar.time
                val randomPoints = generateRandomPoints() // Wygeneruj losowe punkty

                val userPoints = hashMapOf(
                    "memory_points" to randomPoints,
                    "reflex_points" to randomPoints,
                    "observation_points" to randomPoints,
                    "sobriety_points" to randomPoints,
                    "date" to currentDate
                )

                // Dodaj punkty do bazy danych
                firestore.collection("user_points").document(uid).collection("points").add(userPoints)
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Błąd zapisu punktów: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // Odejmij jeden dzień od aktualnej daty
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }
        }
    }

    // Metoda generująca losowe punkty
    private fun generateRandomPoints(): Int {
        return (1..10).random() // Zakres punktów: 1-10
    }
}
