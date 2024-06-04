package com.example.signupapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class wybor_gry : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    var reflexPoints = 0
    var memoryPoints = 0
    var concentrationPoints = 0
    var logicPoints = 0
    companion object {
        const val REQUEST_ROZNICE = 1
        const val REQUEST_UFOLUDKI = 2
        const val REQUEST_KLOCKI = 3
        const val REQUEST_WHACAPIRATE = 4
        const val REQUEST_KOLOR = 5
        const val REQUEST_MAZE = 6
        private const val TAG = "wybor_gry"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wybor_gry)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set Toolbar and add Toggle
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
                            // Save the username to SharedPreferences
                            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("username", username)
                            editor.apply()

                            // Set Toolbar title
                            supportActionBar?.title = "Witaj $username!"

                            // Retrieve today's points after setting the username
                            retrieveTodaysPoints(username)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Nie udało się pobrać danych użytkownika: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Handle NavigationView items
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(applicationContext, succes::class.java)
                    startActivity(intent)
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
                    // Sign out the user and redirect to the login screen
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

        // Initialize dropdowns and buttons
        val dropdown1 = findViewById<RelativeLayout>(R.id.dropdown1)
        val content1 = findViewById<LinearLayout>(R.id.content1)
        dropdown1.setOnClickListener {
            toggleContentVisibility(content1)
        }

        val dropdown2 = findViewById<RelativeLayout>(R.id.dropdown2)
        val content2 = findViewById<LinearLayout>(R.id.content2)
        dropdown2.setOnClickListener {
            toggleContentVisibility(content2)
        }

        val dropdown3 = findViewById<RelativeLayout>(R.id.dropdown3)
        val content3 = findViewById<LinearLayout>(R.id.content3)
        dropdown3.setOnClickListener {
            toggleContentVisibility(content3)
        }

        val dropdown4 = findViewById<RelativeLayout>(R.id.dropdown4)
        val content4 = findViewById<LinearLayout>(R.id.content4)
        dropdown4.setOnClickListener {
            toggleContentVisibility(content4)
        }

        // Handle button clicks
        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            if (reflexPoints == 0) {
                startKolorActivity()
            }
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            if (memoryPoints == 0) {
                //Toast.makeText(this, "Pamięć", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, Memory_MainActivity::class.java)
                startActivity(intent)
            }
        }

        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            if (concentrationPoints == 0) {
                startRozniceActivity()
            }
        }

        val button4 = findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            if (logicPoints == 0) {
                //Toast.makeText(this, "Logika i dedukcja", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, MatematyczneWorlde_MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun retrieveTodaysPoints(username: String) {
        Log.d(TAG, "Starting to retrieve today's points for user: $username")
        val today = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())

        firestore.collection("points")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Successfully retrieved documents.")
                if (documents.isEmpty) {
                    Log.d(TAG, "No points found for today.")
                    Toast.makeText(this, "No points found for today", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "Points found, processing documents.")


                    for (document in documents) {
                        val timestamp = document.getTimestamp("date")
                        if (timestamp != null) {
                            val date = SimpleDateFormat("MMM d", Locale.getDefault()).format(timestamp.toDate())
                            Log.d(TAG, "Comparing date: $date with today: $today")
                            if (date == today) {
                                reflexPoints += document.getLong("reflex_points")?.toInt() ?: 0
                                memoryPoints += document.getLong("memory_points")?.toInt() ?: 0
                                concentrationPoints += document.getLong("perceptiveness_points")?.toInt() ?: 0
                                logicPoints += document.getLong("logic_points")?.toInt() ?: 0
                            }
                        }
                    }

                    if (reflexPoints == 0 && memoryPoints == 0 && concentrationPoints == 0 && logicPoints == 0) {
                        Log.d(TAG, "No points found for today after processing documents.")
                        Toast.makeText(this, "No points found for today", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "Points found: Reflex: $reflexPoints, Memory: $memoryPoints, Concentration: $concentrationPoints, Logic: $logicPoints")
                        updatePointsUI(reflexPoints, memoryPoints, concentrationPoints, logicPoints)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ${exception.message}")
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePointsUI(reflexPoints: Int, memoryPoints: Int, concentrationPoints: Int, logicPoints: Int) {
        findViewById<TextView>(R.id.reflex_points).text = "WYNIK: $reflexPoints"
        findViewById<TextView>(R.id.memory_points).text = "WYNIK: $memoryPoints"
        findViewById<TextView>(R.id.concentration_points).text = "WYNIK: $concentrationPoints"
        findViewById<TextView>(R.id.logic_points).text = "WYNIK: $logicPoints"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleContentVisibility(content: LinearLayout) {
        if (content.visibility == View.GONE) {
            content.visibility = View.VISIBLE
        } else {
            content.visibility = View.GONE
        }
    }

    private fun startRozniceActivity() {
        val intent = Intent(this, Roznice_MainActivity::class.java)
        startActivityForResult(intent, REQUEST_ROZNICE)
    }

    private fun startUfoludkiActivity() {
        val intent = Intent(this, Ufoludki_MainActivity::class.java)
        startActivityForResult(intent, REQUEST_UFOLUDKI)
    }

    private fun startKlockiActivity() {
        val intent = Intent(this, Klocki_MainActivity::class.java)
        startActivityForResult(intent, REQUEST_KLOCKI)
    }

    private fun startWhacAPirateActivity() {
        val intent = Intent(this, WhacAPirateMainActivity::class.java)
        startActivityForResult(intent, REQUEST_WHACAPIRATE)
    }

    private fun startKolorActivity() {
        val intent = Intent(this, Kolor_MainActivity::class.java)
        startActivityForResult(intent, REQUEST_KOLOR)
    }

    private fun startMazeActivity() {
        val intent = Intent(this, Maze_MainActivity::class.java)
        startActivityForResult(intent, REQUEST_MAZE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ROZNICE -> {
                    startUfoludkiActivity()
                }
                REQUEST_UFOLUDKI -> {
                    startKlockiActivity()
                }
                REQUEST_KLOCKI -> {
                    Toast.makeText(this, "All logic and deduction games completed!", Toast.LENGTH_SHORT).show()
                }
                REQUEST_KOLOR -> {
                    startMazeActivity()
                }
                REQUEST_MAZE -> {
                    startWhacAPirateActivity()
                }
                REQUEST_WHACAPIRATE -> {
                    Toast.makeText(this, "All reflex and coordination games completed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
