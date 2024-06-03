package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class succes : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    //private lateinit var lineChartView: LineChartView
    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_succes)

        // Inicjalizacja Firebase Authentication
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // przycisk do rozpoczecia gier
        val przygodaButton = findViewById<Button>(R.id.przygodaButton)

        przygodaButton.setOnClickListener{
            val intent = Intent(applicationContext, Maze_MainActivity::class.java)
            startActivity(intent)
        }

        // Inicjalizacja DrawerLayout i NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            if (year == currentYear && month == currentMonth && dayOfMonth == currentDayOfMonth) {
                val intent = Intent(applicationContext, games::class.java)
                startActivity(intent)
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
        //lineChartView = findViewById(R.id.lineChart)


        // Pobranie bieżącego użytkownika i jego UID

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
                            fetchPointsFromFirestore(uid)
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
                    this.drawerLayout.closeDrawers()
                }
                R.id.nav_rules -> {
                    val intent = Intent(applicationContext, rules::class.java)
                    startActivity(intent)
                }
                R.id.nav_friends -> {
                    val intent = Intent(applicationContext, friends::class.java)
                    startActivity(intent)
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
    private fun fetchPointsFromFirestore(uid: String) {
        val pointsList = mutableListOf<List<Pair<Float, Float>>>()
        val categories = listOf("memory_points", "reflex_points", "observation_points", "sobriety_points")

        // Inicjalizacja pustych list dla każdej kategorii
        val categoryPoints = List(categories.size) { mutableListOf<Pair<Float, Float>>() }

        firestore.collection("user_points").document(uid).collection("points")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(7) // Pobranie ostatnich 7 dni
            .get()
            .addOnSuccessListener { documents ->
                var dayIndex = 0f

                for (document in documents) {
                    for ((index, category) in categories.withIndex()) {
                        val points = document.getLong(category)?.toFloat() ?: 0f
                        categoryPoints[index].add(Pair(dayIndex, points))
                    }
                    dayIndex += 1f
                }

                pointsList.addAll(categoryPoints)

                // Ustawienie danych na wykresie
                //lineChartView.setDataPointsList(pointsList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd podczas pobierania punktów: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}