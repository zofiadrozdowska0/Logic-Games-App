package com.example.signupapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.SetOptions
import kotlin.Pair


class addfriend : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var selectedUserIds: MutableList<String> // Lista przechowująca ID zaznaczonych użytkowników
    private lateinit var invitedUserIds: MutableList<String> // Lista przechowująca ID użytkowników już zaproszonych

    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var linearLayout: LinearLayout
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriend)

        firestore = FirebaseFirestore.getInstance()
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        selectedUserIds = mutableListOf()
        invitedUserIds = mutableListOf()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)

        linearLayout = findViewById(R.id.addfriendView)
        searchView = findViewById(R.id.searchView)

        fetchUsersFromFirestore(linearLayout)

        val addButton: Button = findViewById(R.id.button5)
        addButton.setOnClickListener {
            sendFriendInvitations() // Wywołaj metodę wysyłania zaproszeń do znajomych
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filterUsers(query: String?) {
        linearLayout.removeAllViews()
        val filteredUsers = mutableListOf<Pair<String, String>>()

        for (document in usersList) {
            val userId = document.id
            val userName = document.getString("username")
            val invited = invitedUserIds.contains(userId) // Sprawdzenie czy użytkownik jest już zaproszony
            val isFriend = isFriend(userId) // Sprawdzenie czy użytkownik jest naszym znajomym

            if (userName != null && (query.isNullOrEmpty() || userName.contains(query, true)) && !isFriend && !invited && userId != currentUserId) {
                filteredUsers.add(userId to userName)
            }
        }

        sortUsersAlphabetically(filteredUsers)

        for ((userId, userName) in filteredUsers) {
            val checkBox = CheckBox(this@addfriend)
            checkBox.text = userName
            checkBox.setTextColor(Color.rgb(47, 31, 43))
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUserIds.add(userId)
                } else {
                    selectedUserIds.remove(userId)
                }
            }
            linearLayout.addView(checkBox)
        }
    }

    private fun sortUsersAlphabetically(users: MutableList<Pair<String, String>>) {
        users.sortBy { it.second.toLowerCase() }
    }


    private fun fetchUsersFromFirestore(linearLayout: LinearLayout) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                usersList = documents
                fetchCurrentFriends()
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    private fun fetchCurrentFriends() {
        firestore.collection("friends")
            .whereEqualTo("Inviter", currentUserId)
            .get()
            .addOnSuccessListener { invitations ->
                for (invitation in invitations) {
                    val inviteeId = invitation.getString("Invitee")
                    if (inviteeId != null) {
                        invitedUserIds.add(inviteeId)
                    }
                }
                fetchFriendsList()
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    private fun fetchFriendsList() {
        val currentUserDocRef = firestore.collection("users").document(currentUserId)
        currentUserDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentUserFriends = document.get("friends") as? List<String> ?: listOf()
                    currentFriendsList = currentUserFriends
                    filterUsers(null)
                }
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    private fun isFriend(userId: String): Boolean {
        // Sprawdzenie czy użytkownik jest już naszym znajomym
        return userId in currentFriendsList
    }


    private fun sendFriendInvitations() {
        val inviterId = currentUserId

        for (inviteeId in selectedUserIds) {
            val invitationData = hashMapOf(
                "Inviter" to inviterId,
                "Invitee" to inviteeId
            )

            // Dodaj zaproszenie do kolekcji "friends" dla każdego zaproszonego użytkownika
            firestore.collection("friends").document()
                .set(invitationData, SetOptions.merge())
                .addOnSuccessListener {
                    // Sukces - zaproszenie zostało wysłane
                    invitedUserIds.add(inviteeId)
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                }
        }

        navigateToFriendsActivity() // Przejdź do widoku znajomych po wysłaniu zaproszeń
    }

    private fun navigateToFriendsActivity() {
        val intent = Intent(applicationContext, friends::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private lateinit var usersList: QuerySnapshot
        private lateinit var currentFriendsList: List<String>
    }
}
