package com.example.signupapp

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class refleks_i_koordynacja : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refleks_i_koordynacja)

        // Inicjalizacja Firebase Authentication
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicjalizacja DrawerLayout i NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // obsługa_przycisków
        val button1 = findViewById<Button>(R.id.textView6)
        button1.setOnClickListener {
            // Tworzenie zamiaru celującego w inną aplikację
            val intent = Intent().apply {
                component = ComponentName("com.example.maze", "com.example.maze.MainActivity")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Nie znaleziono aplikacji", Toast.LENGTH_SHORT).show()
            }
        }
        val button2 = findViewById<Button>(R.id.textView7)
        button2.setOnClickListener {
            // Tworzenie zamiaru celującego w inną aplikację
            val intent = Intent().apply {
                component = ComponentName("com.example.kolor", "com.example.kolor.MainActivity")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Nie znaleziono aplikacji", Toast.LENGTH_SHORT).show()
            }
        }
        val button3 = findViewById<Button>(R.id.textView8)
        button3.setOnClickListener {
            // Tworzenie zamiaru celującego w inną aplikację
            val intent = Intent().apply {
                component = ComponentName("com.example.znajdzroznicewalimy", "com.example.znajdzroznicewalimy.MainActivity")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Nie znaleziono aplikacji", Toast.LENGTH_SHORT).show()
            }
        }

        // Ustaw Toolbar i dodaj Toggle
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        if (username != null) {
                            // Ustaw tytuł ToolBar
                            supportActionBar?.title = "Witaj $username!"
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Nie udało się pobrać danych użytkownika: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


        // Obsługa elementów NavigationView
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(applicationContext, succes::class.java)
                    startActivity(intent)
                }
                R.id.nav_rules -> {
                    this.drawerLayout.closeDrawers()
                }
                R.id.nav_friends -> {
                    this.drawerLayout.closeDrawers()
                }
                R.id.nav_logout -> {
                    // Wyloguj użytkownika i przekieruj do ekranu logowania
                    auth.signOut()
                    val intent = Intent(this, login::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

    }
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}





