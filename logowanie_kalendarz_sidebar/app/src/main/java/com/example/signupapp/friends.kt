package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class friends : AppCompatActivity() {

    private lateinit var friendsView: LinearLayout
    private lateinit var fabAddFriend: ImageButton

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserUid: String
    private lateinit var friendsReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendsView = findViewById(R.id.friendsView)
        fabAddFriend = findViewById(R.id.fab_add_friend)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUserUid = firebaseAuth.currentUser?.uid ?: ""

        friendsReference = FirebaseDatabase.getInstance().reference.child("friends").child(currentUserUid)

        fabAddFriend.setOnClickListener {
            startActivity(Intent(this, addfriend::class.java))
        }

        // Pobierz listę znajomych
        retrieveFriends()
    }

    private fun retrieveFriends() {
        friendsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsView.removeAllViews()

                for (friendSnapshot in snapshot.children) {
                    val friendUid = friendSnapshot.key
                    val mutualRelation = friendSnapshot.child("mutualRelation").getValue(Boolean::class.java)

                    // Sprawdź czy istnieje podwójna relacja
                    if (mutualRelation != null && mutualRelation) {
                        // Wyświetl znajomego
                        displayFriend(friendUid)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Obsługa błędu
            }
        })
    }

    private fun displayFriend(friendUid: String?) {
        // Tutaj można dodać logikę wyświetlania znajomego w interfejsie użytkownika
        // Na przykład, utworzyć widok dla każdego znajomego i dodać go do LinearLayout (friendsView)
    }
}




//class friends : AppCompatActivity() {
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var btnadd: android.widget.ImageButton
//    private lateinit var btnadd2: android.widget.ImageButton
//
//    private lateinit var dbHelper: DBHelper
//
//    private lateinit var toggle: ActionBarDrawerToggle
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_friends)
//        dbHelper = DBHelper(this) // Zainicjuj dbHelper
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        val toolbarTitle = dbHelper.getUsernameById(MainActivity.CurrentUser.userId)
//        toolbar.title = toolbarTitle
//        setSupportActionBar(toolbar)
//
//
//
//        val linearLayout: LinearLayout =  findViewById(R.id.friendsView)
//
//        // Pobierz wszystkich znajomych użytkownika z bazy danych
//        val userFriends = dbHelper.getUserFriends(MainActivity.CurrentUser.userId)
//
//        for (friendId in userFriends) {
//            val textView = TextView(this)
//            val friendName = dbHelper.getUsernameById(friendId)
//            if (dbHelper.areFriends(MainActivity.CurrentUser.userId, friendId) && dbHelper.areFriends(friendId, MainActivity.CurrentUser.userId)) {
//                // Jeśli relacja znajomości jest obustronna
//                textView.text = friendName
//            } else {
//                // Jeśli relacja znajomości jest jednostronna
//                textView.text = "[$friendName] - zaproszenie wysłane"
//            }
//            textView.textSize = 22f
//            textView.setPadding(18, 18, 16, 16)
//            linearLayout.addView(textView)
//            textView.setOnClickListener {
//                val intent = Intent(applicationContext, checkfriend::class.java)
//                startActivity(intent)
//            }
//        }
//
//        btnadd = findViewById(R.id.fab_add_friend)
//        btnadd2 = findViewById(R.id.fab_friendlist)
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
//
//        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba_navbar)
//
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_home -> {
//                    val intent = Intent(applicationContext, succes::class.java)
//                    startActivity(intent)
//                }
//                R.id.nav_rules -> {
//                    val intent = Intent(applicationContext, rules::class.java)
//                    startActivity(intent)
//                }
//                R.id.nav_friends -> {
//                    val intent = Intent(applicationContext, friends::class.java)
//                    startActivity(intent)
//                }
//                R.id.nav_logout -> {
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }
//        btnadd.setOnClickListener{
//            val intent = Intent(this, addfriend::class.java)
//            startActivity(intent)
//        }
//        btnadd2.setOnClickListener{
//            val intent = Intent(this, friendinv::class.java)
//            startActivity(intent)
//        }
//    }
//
//    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}
//
