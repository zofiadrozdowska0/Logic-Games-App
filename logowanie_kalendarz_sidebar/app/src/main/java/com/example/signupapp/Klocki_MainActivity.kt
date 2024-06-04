package com.example.signupapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.signupapp.databinding.KlockiActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class Klocki_MainActivity : AppCompatActivity() {
    lateinit var binding: KlockiActivityMainBinding
    private var dx = 0f
    private var dy = 0f
    private var dz = 1f
    private var orgx = 0f
    private var orgy = 0f
    private var startTime: Long = SystemClock.elapsedRealtime()
    private var wins = 0
    private var prev_task = 0

    private var ImageView.rotated: Boolean
        get() = tag as? Boolean ?: false // Pobiera wartość z tagu, domyślnie false
        set(value) { tag = value } // Ustawia wartość jako tag obrazka

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KlockiActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ukryj pasek stanu
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN // Ustaw flagę, aby ukryć pasek stanu
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        load_task()

        val touchListener = View.OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dx = view.x - event.rawX
                    dy = view.y - event.rawY
                    dz += 1f
                    orgx = view.x
                    orgy = view.y
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dx)
                        .y(event.rawY + dy)
                        .z(dz)
                    view.animate().setDuration(0).start()
                }
                else -> {
                    if (orgx - view.x <= 3f && orgy - view.y <= 3f) {
                        view.animate().setDuration(60)
                        view.animate().rotationBy(90f)
                        if (view.rotation % 90 != 0f) //zabezpieczenie przed zatrzymaniem animacji przed wykonaniem całego obrotu
                        {
                            view.animate().rotationBy(90 - (view.rotation % 90))
                            if (view is ImageView && view.tag != 'o') {
                                view.rotated = !view.rotated
                            }
                        }
                        dz += 1f
                        view.animate().z(dz)
                        if (view is ImageView && view.tag != 'o') {
                            view.rotated = !view.rotated
                        }
                    }
                    grid(binding.imageView2)
                    grid(binding.imageView3)
                    grid(binding.imageView4)
                    grid(binding.imageView5)
                    checkWin()
                    supportActionBar?.hide()
                    return@OnTouchListener false
                }
            }
            true
        }

        binding.imageView2.setOnTouchListener(touchListener)
        binding.imageView3.setOnTouchListener(touchListener)
        binding.imageView4.setOnTouchListener(touchListener)
        binding.imageView5.setOnTouchListener(touchListener)
    }

    fun moveImageInDp(imageView: ImageView, dxInDp: Float, dyInDp: Float) {
        val dxInPixels = dpToPixels(dxInDp)
        val dyInPixels = dpToPixels(dyInDp)
        imageView.x = dxInPixels
        imageView.y = dyInPixels
    }

    fun dpToPixels(dp: Float): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
    }

    fun grid(view: View) {
        if (view.y < dpToPixels(270f)) {
            if (view is ImageView) {
                if (!view.rotated) {
                    moveImageInDp(
                        view,
                        findNearestValue(pixelsToDp(view.x).toInt()).toFloat(),
                        findNearestValue3(pixelsToDp(view.y).toInt()).toFloat()
                    )
                } else {
                    moveImageInDp(
                        view,
                        findNearestValue2(pixelsToDp(view.x).toInt()).toFloat(),
                        findNearestValue4(pixelsToDp(view.y).toInt()).toFloat()
                    )
                }
            }
        }
    }

    fun pixelsToDp(pixels: Float): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return pixels / (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun getScreenBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun getPixelColor(bitmap: Bitmap, x: Int, y: Int): Int {
        return bitmap.getPixel(x, y)
    }

    fun decodeColor(color: Int): Triple<Int, Int, Int> {
        val alpha = color shr 24 and 0xFF
        val red = color shr 16 and 0xFF
        val green = color shr 8 and 0xFF
        val blue = color and 0xFF
        return Triple(red, green, blue)
    }

    fun checkWin(): Boolean {
        val bitmap = getScreenBitmap(window.decorView.rootView)
        val values_horizontal = arrayOf(108f, 170f, 233f, 295f)
        val values_vertical = arrayOf(43f, 105f, 168f, 230f)
        var filledRect = 0
        for (i in values_horizontal) {
            for (j in values_vertical) {
                val color = getPixelColor(bitmap, dpToPixels(i).toInt(), dpToPixels(j).toInt())
                val (red, green, blue) = decodeColor(color)
                if ((red == 240 && green == 0 && blue == 0) || (red == 0 && green == 240 && blue == 0) || (red == 0 && green == 0 && blue == 240) || (red == 0 && green == 240 && blue == 240) || (red == 160 && green == 0 && blue == 240) || (red == 240 && green == 160 && blue == 0) || (red == 240 && green == 240 && blue == 0)) {
                    filledRect += 1
                }
            }
        }
        if (filledRect == 16) {
            wins++
            if (wins >= 5) {
                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                val seconds = (elapsedTime / 1000).toInt()
                val tenthsseconds = (elapsedTime / 100).toInt()
                val timeString = String.format("%02d.%01d", seconds, tenthsseconds % 10)
                println("Warunki Wygranej. Czas gry: $timeString")
                binding.textView.text =  "Brawo!\nCzas: $timeString s"
                binding.textView.x = dpToPixels(34f)
                binding.textView.y = dpToPixels(410f)
                binding.textView.textSize = 50f
                binding.textView.visibility = View.VISIBLE
                binding.imageView6.z = ++dz
                binding.imageView6.visibility = View.VISIBLE
                binding.imageView2.setOnTouchListener(null)
                binding.imageView3.setOnTouchListener(null)
                binding.imageView4.setOnTouchListener(null)
                binding.imageView5.setOnTouchListener(null)

                val points = calculatePoints()
                savePointsToSharedPreferences("klocki_points", points)

                Handler(Looper.getMainLooper()).postDelayed({
                    showAllGamesCompletedDialog()
                }, 2000) // Show the "All games completed!" dialog after 2 seconds

                return true
            } else {
                load_task()
            }
        }
        return false
    }

    private fun showAllGamesCompletedDialog() {
        AlertDialog.Builder(this)
            .setTitle("All games completed!")
            .setMessage("Congratulations! You have completed all games.")
            .setPositiveButton("OK") { _, _ ->
                saveTotalPointsToDatabase()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun saveTotalPointsToDatabase() {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val roznicePoints = sharedPreferences.getInt("roznice_points", 0)
        val ufoludkiPoints = sharedPreferences.getInt("ufoludki_points", 0)
        val klockiPoints = sharedPreferences.getInt("klocki_points", 0)
        val totalPoints = roznicePoints + ufoludkiPoints + klockiPoints

        // Retrieve the username from SharedPreferences
        val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "Unknown User")

        val db = FirebaseFirestore.getInstance()
        val pointsData = hashMapOf(
            "username" to username,
            "perceptiveness_and_concentration_points" to totalPoints,
            "date" to com.google.firebase.Timestamp.now()
        )

        db.collection("points")
            .add(pointsData)
            .addOnSuccessListener {
                println("Points successfully written!")
            }
            .addOnFailureListener { e ->
                println("Error writing document: $e")
            }
    }

    private fun savePointsToSharedPreferences(key: String, points: Int) {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, points)
        editor.apply()
    }

    private fun calculatePoints(): Int {
        // Implement your logic to calculate points
        return wins * 10 // Example: each win gives 10 points
    }

    fun findNearestValue(input: Int): Int {
        val values = listOf(80, 142, 205, 267)
        var nearestValue = values.first()
        var minDifference = Math.abs(input - nearestValue)

        for (value in values) {
            val difference = Math.abs(input - value)
            if (difference < minDifference) {
                minDifference = difference
                nearestValue = value
            }
        }
        return nearestValue
    }

    fun findNearestValue2(input: Int): Int {
        val values = listOf(80 - 125 / 4, 142 - 125 / 4, 205 - 125 / 4, 267 - 125 / 4)
        var nearestValue = values.first()
        var minDifference = Math.abs(input - nearestValue)

        for (value in values) {
            val difference = Math.abs(input - value)
            if (difference < minDifference) {
                minDifference = difference
                nearestValue = value
            }
        }
        return nearestValue
    }

    fun findNearestValue3(input: Int): Int {
        val values = listOf(16, 78, 141, 203)
        var nearestValue = values.first()
        var minDifference = Math.abs(input - nearestValue)

        for (value in values) {
            val difference = Math.abs(input - value)
            if (difference < minDifference) {
                minDifference = difference
                nearestValue = value
            }
        }
        return nearestValue
    }

    fun findNearestValue4(input: Int): Int {
        val values = listOf(-46 - 125 / 4, 16 - 125 / 4, 78 - 125 / 4, 141 - 125 / 4, 203 - 125 / 4)
        var nearestValue = values.first()
        var minDifference = Math.abs(input - nearestValue)

        for (value in values) {
            val difference = Math.abs(input - value)
            if (difference < minDifference) {
                minDifference = difference
                nearestValue = value
            }
        }
        return nearestValue
    }

    fun load_task() {
        moveImageInDp(binding.imageView1, 80f, 16f)

        moveImageInDp(binding.imageView2, 31f, 278f)
        binding.imageView2.layoutParams.height = dpToPixels(187.5f).toInt()
        binding.imageView2.layoutParams.width = dpToPixels(125f).toInt()
        binding.imageView2.rotation = 0f
        binding.imageView2.rotated = false

        moveImageInDp(binding.imageView3, 233f, 278f)
        binding.imageView3.layoutParams.height = dpToPixels(250f).toInt()
        binding.imageView3.layoutParams.width = dpToPixels(62.5f).toInt()
        binding.imageView3.rotation = 0f
        binding.imageView3.rotated = false

        moveImageInDp(binding.imageView4, 31f, 528f)
        binding.imageView4.layoutParams.height = dpToPixels(187.5f).toInt()
        binding.imageView4.layoutParams.width = dpToPixels(125f).toInt()
        binding.imageView4.rotation = 0f
        binding.imageView4.rotated = false

        moveImageInDp(binding.imageView5, 252f, 528f)
        binding.imageView5.layoutParams.height = dpToPixels(187.5f).toInt()
        binding.imageView5.layoutParams.width = dpToPixels(125f).toInt()
        binding.imageView5.rotation = 0f
        binding.imageView5.rotated = false

        var randomNumber = Random.nextInt(1, 16)
        while (randomNumber == prev_task) {
            randomNumber = Random.nextInt(1, 16)
        }
        prev_task = randomNumber
        when (randomNumber) {
            1 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_o)
                binding.imageView5.layoutParams.height = dpToPixels(125f).toInt()
                binding.imageView5.rotated = false
                binding.imageView5.tag = 'o'
            }
            2 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_z)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_l)
            }
            3 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_s)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_l)
            }
            4 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_t)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l)
                binding.imageView5.setImageResource(R.drawable.klocki_t)
            }
            5 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_t)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_t)
            }
            6 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_t)
                binding.imageView3.setImageResource(R.drawable.klocki_s)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_t)
            }
            7 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_l)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_l2)
            }
            8 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_l)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_o)
                binding.imageView4.layoutParams.height = dpToPixels(125f).toInt()
                binding.imageView4.rotated = false
                binding.imageView4.tag = 'o'
                binding.imageView5.setImageResource(R.drawable.klocki_o)
                binding.imageView5.layoutParams.height = dpToPixels(125f).toInt()
                binding.imageView5.rotated = false
                binding.imageView5.tag = 'o'
            }
            9 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_l)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_z)
                binding.imageView5.setImageResource(R.drawable.klocki_z)
            }
            10 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l2)
                binding.imageView3.setImageResource(R.drawable.klocki_l2)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_s)
                binding.imageView5.setImageResource(R.drawable.klocki_s)
            }
            11 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l2)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_z)
                binding.imageView5.setImageResource(R.drawable.klocki_l2)
            }
            12 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_s)
                binding.imageView5.setImageResource(R.drawable.klocki_l)
            }
            13 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_t)
                binding.imageView3.setImageResource(R.drawable.klocki_z)
                binding.imageView3.layoutParams.height = dpToPixels(187.5f).toInt()
                binding.imageView3.layoutParams.width = dpToPixels(125f).toInt()
                binding.imageView4.setImageResource(R.drawable.klocki_t)
                binding.imageView5.setImageResource(R.drawable.klocki_l)
            }
            14 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l2)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l2)
                binding.imageView5.setImageResource(R.drawable.klocki_o)
                binding.imageView5.layoutParams.height = dpToPixels(125f).toInt()
                binding.imageView5.rotated = false
                binding.imageView5.tag = 'o'
            }
            15 -> {
                binding.imageView2.setImageResource(R.drawable.klocki_l)
                binding.imageView3.setImageResource(R.drawable.klocki_i)
                binding.imageView4.setImageResource(R.drawable.klocki_l)
                binding.imageView5.setImageResource(R.drawable.klocki_o)
                binding.imageView5.layoutParams.height = dpToPixels(125f).toInt()
                binding.imageView5.rotated = false
                binding.imageView5.tag = 'o'
            }
        }
    }
}
