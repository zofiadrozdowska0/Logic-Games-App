package com.example.signupapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
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
        setContentView(R.layout.activity_friendinv)

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

        val linearLayout: LinearLayout = findViewById(R.id.friendinvView)

        fetchInvitersFromFirestore(linearLayout)

        val addButton: Button = findViewById(R.id.button6)
        addButton.setOnClickListener {
            sendFriendInvitations() // Wywołaj metodę wysyłania zaproszeń do znajomych
        }

        val removeButton: Button = findViewById(R.id.button7)
        removeButton.setOnClickListener {
            removeSelectedInvitations() // Wywołaj metodę usuwania zaproszeń do znajomych
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
                    Toast.makeText(this@friendinv, "Zaproszenie przyjęto", Toast.LENGTH_SHORT).show()
                    invitedUserIds.add(inviteeId)
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                }
        }

        navigateToFriendsActivity() // Przejdź do widoku znajomych po wysłaniu zaproszeń
    }

    private fun removeSelectedInvitations() {
        selectedUserIds.forEach { userId ->
            firestore.collection("friends")
                .whereEqualTo("Invitee", currentUserId)
                .whereEqualTo("Inviter", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        firestore.collection("friends").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this@friendinv, "Zaproszenie usunięto", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                            }
                    }
                }
                .addOnFailureListener { e ->
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
