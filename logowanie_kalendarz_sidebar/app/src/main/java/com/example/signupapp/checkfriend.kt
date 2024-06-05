package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

class checkfriend : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var dataPointsList: List<List<Pair<Float, Float>>> = emptyList()
    private var username: String? = null  // To hold the friend's username
    private lateinit var currentUserId: String
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendcheck)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        username = intent.getStringExtra("FRIEND_USERNAME")
        if (username.isNullOrEmpty()) {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()  // Close the activity if no username
        } else {
            supportActionBar?.title = "$username"
            fetchDataPointsFromDatabase()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
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
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val removeFriendButton = findViewById<Button>(R.id.remove_friend_Button)
        removeFriendButton.setOnClickListener {
            findUserIdByUsername(username.toString()) { userId ->
                if (userId != null) {
                    removeFriend(userId)
                } else {
                    Toast.makeText(this, "No user found with that username.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFriend(friend_id: String) {

        // Usuń rekordy gdzie currentUserId to Invitee, a friend_id to Inviter
        firestore.collection("friends")
            .whereEqualTo("Invitee", currentUserId)
            .whereEqualTo("Inviter", friend_id)
            .get()
            .addOnSuccessListener { documents ->
                removeDocuments(documents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching original documents: ${e.message}", Toast.LENGTH_LONG).show()
            }

        // Usuń rekordy gdzie friend_id to Invitee, a currentUserId to Inviter
        firestore.collection("friends")
            .whereEqualTo("Invitee", friend_id)
            .whereEqualTo("Inviter", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                removeDocuments(documents)
                Toast.makeText(this, "Usunieto znajomego", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching reversed documents: ${e.message}", Toast.LENGTH_LONG).show()
            }

        Thread.sleep(300)
        navigateToFriendsActivity() // Navigate to the friends view after handling the invitation
    }

    private fun removeDocuments(documents: QuerySnapshot) {
        if (documents.isEmpty) {
            return
        }
        for (document in documents) {
            firestore.collection("friends").document(document.id)
                .delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error removing invitation: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }


    private fun fetchDataPointsFromDatabase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("points")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val pointsMap = processResult(result)
                val dataPoints = mapToPointsList(pointsMap)
                setDataPointsList(dataPoints)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: $exception", Toast.LENGTH_LONG).show()
            }
    }

    private fun processResult(result: QuerySnapshot): Map<String, Pair<Date, Float>> {
        val pointsMap = mutableMapOf<String, Pair<Date, Float>>()
        for (document in result) {
            val date = document.getTimestamp("date")?.toDate()
            val reflexPointsValue = document.getLong("reflex_points")?.toFloat() ?: 0f

            date?.let {
                val formattedDate = formatDate(it)
                updatePointsMap(pointsMap, formattedDate, it, reflexPointsValue)
            }
        }
        return pointsMap
    }

    private fun updatePointsMap(pointsMap: MutableMap<String, Pair<Date, Float>>, formattedDate: String, dateObj: Date, pointsValue: Float) {
        val currentEntry = pointsMap[formattedDate]
        if (currentEntry == null || dateObj.after(currentEntry.first)) {
            pointsMap[formattedDate] = Pair(dateObj, pointsValue)
        }
    }

    private fun mapToPointsList(pointsMap: Map<String, Pair<Date, Float>>): List<List<Pair<Float, Float>>> {
        val list = mutableListOf<Pair<Float, Float>>()
        pointsMap.forEach { (_, pair) ->
            list.add(Pair(getDayIndex(formatDate(pair.first)).toFloat(), pair.second))
        }
        return listOf(list)
    }

    private fun setDataPointsList(dataPointsList: List<List<Pair<Float, Float>>>) {
        this.dataPointsList = dataPointsList
        // Here you would update your view with the new data points
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getDayIndex(formattedDate: String): Int {
        val dates = getDatesFromLast7Days()
        return dates.indexOf(formattedDate)
    }

    private fun getDatesFromLast7Days(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        for (i in 0..6) {
            dates.add(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time))
            calendar.add(Calendar.DATE, -1)
        }
        return dates
    }

    fun findUserIdByUsername(username: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("username", username)
            .limit(1)  // Zakładamy, że nazwy użytkowników są unikalne
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null)  // Brak użytkownika o tej nazwie
                    return@addOnSuccessListener
                }
                for (document in documents) {
                    val userId = document.id  // Pobierz ID użytkownika
                    callback(userId)
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                callback(null)  // Obsługa błędu, zwróć null
            }
    }
    private fun navigateToFriendsActivity() {
        val intent = Intent(applicationContext, friends::class.java)
        startActivity(intent)
        finish()
    }
}
