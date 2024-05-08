package com.example.signupapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class friendinv : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var selectedUserIds: MutableList<String> // Lista przechowująca ID zaznaczonych użytkowników
    private lateinit var invitedUserIds: MutableList<String> // Lista przechowująca ID użytkowników już zaproszonych

    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String

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

        val linearLayout: LinearLayout = findViewById(R.id.addfriendView)

        fetchInvitersFromFirestore(linearLayout)

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
                    // Nie trzeba ponownie uruchamiać tej samej aktywności
                    drawerLayout.closeDrawer(GravityCompat.START)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchInvitersFromFirestore(linearLayout: LinearLayout) {
        firestore.collection("friends")
            .whereEqualTo("Invitee", currentUserId)
            .get()
            .addOnSuccessListener { invitations ->
                val inviters = mutableListOf<String>()
                val myInviters = mutableSetOf<String>()

                // Dodaj do listy użytkowników, którzy wysłali zaproszenie do bieżącego użytkownika
                for (invitation in invitations) {
                    val inviterId = invitation.getString("Inviter")
                    if (inviterId != null) {
                        inviters.add(inviterId)
                    }
                }

                // Pobierz zaproszenia wysłane przez bieżącego użytkownika
                firestore.collection("friends")
                    .whereEqualTo("Inviter", currentUserId)
                    .get()
                    .addOnSuccessListener { myInvitations ->
                        for (invitation in myInvitations) {
                            val inviteeId = invitation.getString("Invitee")
                            if (inviteeId != null) {
                                myInviters.add(inviteeId)
                            }
                        }

                        // Usuń z listy użytkowników, z którymi mamy obustronne zaproszenie
                        inviters.removeAll(myInviters)

                        fetchUserNamesFromFirestore(inviters.toList(), linearLayout)
                    }
                    .addOnFailureListener { exception ->
                        // Obsługa błędu
                    }
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }


    private fun fetchUserNamesFromFirestore(userIDs: List<String>, linearLayout: LinearLayout) {
        for (userID in userIDs) {
            firestore.collection("users").document(userID)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("username")
                    if (userName != null) {
                        val checkBox = CheckBox(this@friendinv)
                        checkBox.text = userName
                        checkBox.setTextColor(Color.rgb(47, 31, 43))
                        checkBox.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedUserIds.add(userID)
                            } else {
                                selectedUserIds.remove(userID)
                            }
                        }
                        linearLayout.addView(checkBox)
                    }
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                }
        }
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
}


//package com.example.signupapp
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.LinearLayout
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.navigation.NavigationView
//
//class friendinv : AppCompatActivity() {
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var toggle: ActionBarDrawerToggle
//    private lateinit var dbHelper: DBHelper
//    private lateinit var selectedUserIds: MutableList<Int> // Lista przechowująca ID zaznaczonych użytkowników
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_addfriend)
//        dbHelper = DBHelper(this)
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        val toolbarTitle = dbHelper.getUsernameById(MainActivity.CurrentUser.userId)
//        toolbar.title = toolbarTitle
//        setSupportActionBar(toolbar)
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
//
//
//        selectedUserIds = mutableListOf()
//
//        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)
//
//        val linearLayout: LinearLayout = findViewById(R.id.addfriendView)
//
//        val usernames = dbHelper.getInvitedFriends()
//
//        for (username in usernames) {
//            val checkBox = CheckBox(this)
//            checkBox.text = username
//            checkBox.setOnCheckedChangeListener { _, isChecked ->
//                val userId = dbHelper.getUserIdByUsername(username) // Pobierz ID użytkownika na podstawie nazwy użytkownika
//                if (isChecked) {
//                    userId?.let { selectedUserIds.add(it) } // Dodaj ID zaznaczonego użytkownika do listy, jeśli nie jest null
//                } else {
//                    userId?.let { selectedUserIds.remove(it) } // Usuń ID zaznaczonego użytkownika z listy, jeśli nie jest null
//                }
//            }
//            linearLayout.addView(checkBox)
//        }
//
//        val addButton: Button = findViewById(R.id.button5)
//        addButton.setOnClickListener {
//            addSelectedFriendsToDatabase() // Wywołaj metodę dodawania wybranych znajomych do bazy danych
//        }
//
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_home -> {
//                    drawerLayout.closeDrawer(GravityCompat.START)
//                }
//                R.id.nav_rules -> {
//                    val intent = Intent(applicationContext, rules::class.java)
//                    startActivity(intent)
//                }
//                R.id.nav_friends -> {
//                    // Nie trzeba ponownie uruchamiać tej samej aktywności
//                    drawerLayout.closeDrawer(GravityCompat.START)
//                }
//                R.id.nav_logout -> {
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun addSelectedFriendsToDatabase() {
//        val userId = MainActivity.CurrentUser.userId // Pobierz ID aktualnie zalogowanego użytkownika
//        if (userId != -1) {
//            for (selectedUserId in selectedUserIds) {
//                dbHelper.addFriend(userId, selectedUserId) // Dodaj każdego wybranego znajomego do bazy danych
//            }
//            navigateToFriendsActivity() // Przejdź do widoku znajomych
//        }
//    }
//
//    private fun getCurrentUserId(): Int {
//        return intent.getIntExtra("USER_ID", -1) // Przykładowe ID użytkownika
//    }
//
//    private fun navigateToFriendsActivity() {
//        val intent = Intent(applicationContext, friends::class.java)
//        startActivity(intent)
//        finish() // Zakończ bieżącą aktywność, aby użytkownik nie mógł wrócić do niej po przejściu do widoku znajomych
//    }
//}
