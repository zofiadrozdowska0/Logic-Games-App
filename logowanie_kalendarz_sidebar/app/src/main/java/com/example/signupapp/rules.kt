package com.example.signupapp
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class rules : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
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
