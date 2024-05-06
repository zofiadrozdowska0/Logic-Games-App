package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class friends : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnadd: FloatingActionButton
    private lateinit var btnadd2: FloatingActionButton

    private lateinit var dbHelper: DBHelper

    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        dbHelper = DBHelper(this) // Zainicjuj dbHelper
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarTitle = dbHelper.getUsernameById(MainActivity.CurrentUser.userId)
        toolbar.title = toolbarTitle
        setSupportActionBar(toolbar)



        val linearLayout: LinearLayout =  findViewById(R.id.friendsView)

        // Pobierz wszystkich znajomych użytkownika z bazy danych
        val userFriends = dbHelper.getUserFriends(MainActivity.CurrentUser.userId)

        for (friendId in userFriends) {
            val textView = TextView(this)
            val friendName = dbHelper.getUsernameById(friendId)
            if (dbHelper.areFriends(MainActivity.CurrentUser.userId, friendId) && dbHelper.areFriends(friendId, MainActivity.CurrentUser.userId)) {
                // Jeśli relacja znajomości jest obustronna
                textView.text = friendName
            } else {
                // Jeśli relacja znajomości jest jednostronna
                textView.text = "[$friendName] - zaproszenie wysłane"
            }
            textView.textSize = 22f
            textView.setPadding(18, 18, 16, 16)
            linearLayout.addView(textView)
            textView.setOnClickListener {
                val intent = Intent(applicationContext, checkfriend::class.java)
                startActivity(intent)
            }
        }

        btnadd = findViewById(R.id.fab_add_friend)
        btnadd2 = findViewById(R.id.fab_friendlist)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba1)

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
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
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

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

