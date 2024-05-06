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

class checkfriend : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendcheck)
        dbHelper = DBHelper(this) // Zainicjuj dbHelper
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarTitle = dbHelper.getUsernameById(MainActivity.CurrentUser.userId)
        toolbar.title = toolbarTitle
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_ryba1)  // Ikona menu hamburger (dodaj plik do drawable)

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
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
