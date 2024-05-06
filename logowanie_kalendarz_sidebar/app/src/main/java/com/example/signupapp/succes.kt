package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar


class succes : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var lineChartView: LineChartView // Dodaj deklarację zmiennej lineChartView
    private lateinit var dbHelper: DBHelper // Deklarujesz obiekt klasy DBHelper

    private val categories = listOf("reflex_points", "memory_points", "concentration_points", "logic_points")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_succes)

        dbHelper = DBHelper(this)

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
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        lineChartView = findViewById(R.id.lineChart)

        val userId = MainActivity.CurrentUser.userId

        // Pobieramy dane z bazy danych dla każdej kategorii punktów
        val reflexPointsHistory = getPointsHistory(userId, "reflex_points")
        val memoryPointsHistory = getPointsHistory(userId, "memory_points")
        val concentrationPointsHistory = getPointsHistory(userId, "concentration_points")
        val logicPointsHistory = getPointsHistory(userId, "logic_points")

        // Przekazujemy dane do LineChartView
        val dataPointsList = mutableListOf<List<Pair<Float, Float>>>()
        dataPointsList.add(processDataForChart(reflexPointsHistory))
        dataPointsList.add(processDataForChart(memoryPointsHistory))
        dataPointsList.add(processDataForChart(concentrationPointsHistory))
        dataPointsList.add(processDataForChart(logicPointsHistory))

        // Dodajemy printowanie punktów
        for ((index, points) in dataPointsList.withIndex()) {
            println("Kategoria punktów: ${categories[index]}")
            for (point in points) {
                println("Punkt: ${point.first}, ${point.second}")
            }
        }

        lineChartView.setDataPointsList(dataPointsList)
    }


    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun processDataForChart(pointsHistory: List<Pair<String, Int>>): List<Pair<Float, Float>> {
        // Przetwarzamy datę na float, aby można ją było wyświetlić na osi X
        // Tworzymy listę par (dataFloat, punkty) dla wykresu
        val dataPoints = mutableListOf<Pair<Float, Float>>()
        var count = 0f
        for (entry in pointsHistory) {
            dataPoints.add(count to entry.second.toFloat())
            count += 1f
        }
        return dataPoints
    }

    private fun getPointsHistory(userId: Int, category: String): List<Pair<String, Int>> {
        val pointsHistory = mutableListOf<Pair<String, Int>>()
        val db = dbHelper.writableDatabase

        // Tworzymy zapytanie SQL w zależności od kategorii
        val query = "SELECT date, $category FROM PointsHistory WHERE user_id = $userId"

        // Wykonujemy zapytanie i pobieramy wyniki
        val cursor = db.rawQuery(query, null)
        cursor.use {
            // Sprawdzamy, czy wyniki są niepuste
            if (it.moveToFirst()) {
                val dateIndex = it.getColumnIndex("date")
                val pointsIndex = it.getColumnIndex(category)

                do {
                    val date = it.getString(dateIndex)
                    val points = it.getInt(pointsIndex)
                    pointsHistory.add(date to points)
                } while (it.moveToNext())
            }
        }

        return pointsHistory
    }

}