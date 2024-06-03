package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class wybor_gry : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        const val REQUEST_ROZNICE = 1
        const val REQUEST_UFOLUDKI = 2
        const val REQUEST_KLOCKI = 3
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
                            // Set Toolbar title
                            supportActionBar?.title = "Witaj $username!"
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
                    this.drawerLayout.closeDrawers()
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
            Toast.makeText(this, "refleks", Toast.LENGTH_SHORT).show()
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            Toast.makeText(this, "Pamięć", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, Memory_MainActivity::class.java)
            startActivity(intent)
        }

        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            startRozniceActivity()
        }

        val button4 = findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            Toast.makeText(this, "Logika i dedukcja", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, MatematyczneWorlde_MainActivity::class.java)
            startActivity(intent)
        }
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
                    Toast.makeText(this, "All games completed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
