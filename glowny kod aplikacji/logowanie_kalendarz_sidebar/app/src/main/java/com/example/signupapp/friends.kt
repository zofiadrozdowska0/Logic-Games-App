package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class friends : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnadd: ImageButton
    private lateinit var btnadd2: ImageButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserID: String

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid ?: ""

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val linearLayout: LinearLayout = findViewById(R.id.friendsView)

        fetchUserFriendsFromFirestore(linearLayout)

        btnadd = findViewById(R.id.fab_add_friend)
        btnadd2 = findViewById(R.id.fab_friendlist)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)

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
        btnadd.setOnClickListener{
            val intent = Intent(this, addfriend::class.java)
            startActivity(intent)
        }
        btnadd2.setOnClickListener{
            val intent = Intent(this, friendinv::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchUserFriendsFromFirestore(linearLayout: LinearLayout) {
        firestore.collection("friends")
            .whereEqualTo("Inviter", currentUserID)
            .get()
            .addOnSuccessListener { documents ->
                val invitationsSent = mutableListOf<String>()
                for (document in documents) {
                    val inviteeID = document.getString("Invitee")
                    if (inviteeID != null) {
                        invitationsSent.add(inviteeID)
                    }
                }
                fetchAcceptedFriendsFromFirestore(invitationsSent, linearLayout)
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    private fun fetchAcceptedFriendsFromFirestore(invitationsSent: List<String>, linearLayout: LinearLayout) {
        firestore.collection("friends")
            .whereEqualTo("Invitee", currentUserID)
            .get()
            .addOnSuccessListener { documents ->
                val invitationsReceived = mutableListOf<String>()
                for (document in documents) {
                    val inviterID = document.getString("Inviter")
                    if (inviterID != null) {
                        invitationsReceived.add(inviterID)
                    }
                }
                val mutualFriends = invitationsSent.intersect(invitationsReceived)
                val invitationsNotMutual = invitationsSent.subtract(invitationsReceived)
                fetchUserNamesFromFirestore(mutualFriends.toList(), linearLayout)
                displayInvitationsNotMutual(invitationsNotMutual.toList(), linearLayout)
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    private fun displayInvitationsNotMutual(invitationsNotMutual: List<String>, linearLayout: LinearLayout) {
        for (userID in invitationsNotMutual) {
            firestore.collection("users").document(userID)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("username")
                    if (userName != null) {
                        val textView = TextView(this)
                        textView.text = "[$userName] - zaproszenie wysłane"
                        textView.textSize = 22f
                        textView.setPadding(18, 18, 16, 16)
                        linearLayout.addView(textView)
                        textView.setOnClickListener {
                            removeFriend(userID)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                }
        }
    }


    private fun fetchUserNamesFromFirestore(userIDs: List<String>, linearLayout: LinearLayout) {
        for (userID in userIDs) {
            firestore.collection("users").document(userID)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("username")
                    if (userName != null) {
                        val textView = TextView(this)
                        textView.text = userName
                        textView.textSize = 22f
                        textView.setPadding(18, 18, 16, 16)
                        linearLayout.addView(textView)
                        textView.setOnClickListener {
                            val intent = Intent(this, checkfriend::class.java)
                            intent.putExtra("FRIEND_USERNAME", userName)  // Pass the friend's username
                            startActivity(intent)
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                }
        }
    }

    private fun removeFriend(friend_id: String) {

        // Usuń rekordy gdzie currentUserId to Invitee, a friend_id to Inviter
        firestore.collection("friends")
            .whereEqualTo("Invitee",currentUserID)
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
            .whereEqualTo("Inviter", currentUserID)
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

    private fun navigateToFriendsActivity() {
        val intent = Intent(applicationContext, friends::class.java)
        startActivity(intent)
        finish()
    }
}



