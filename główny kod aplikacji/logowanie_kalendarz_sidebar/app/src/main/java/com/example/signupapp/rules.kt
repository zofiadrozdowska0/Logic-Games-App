package com.example.signupapp
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class rules : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        // Inicjalizacja Firebase Authentication
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicjalizacja DrawerLayout i NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

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
        val dropdown1 = findViewById<RelativeLayout>(R.id.dropdown1)
        val content1 = findViewById<TextView>(R.id.content1)
        dropdown1.setOnClickListener {
            toggleContentVisibility(content1)
        }
        val dropdown2 = findViewById<RelativeLayout>(R.id.dropdown2)
        val content2 = findViewById<TextView>(R.id.content2)
        dropdown2.setOnClickListener {
            toggleContentVisibility(content2)
        }
        val dropdown3 = findViewById<RelativeLayout>(R.id.dropdown3)
        val content3 = findViewById<TextView>(R.id.content3)
        dropdown3.setOnClickListener {
            toggleContentVisibility(content3)
        }
        val dropdown4 = findViewById<RelativeLayout>(R.id.dropdown4)
        val content4 = findViewById<TextView>(R.id.content4)
        dropdown4.setOnClickListener {
            toggleContentVisibility(content4)
        }

    }
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun toggleContentVisibility(content: TextView) {
        if (content.visibility == View.GONE) {
            content.visibility = View.VISIBLE
        } else {
            content.visibility = View.GONE
        }
    }
}
