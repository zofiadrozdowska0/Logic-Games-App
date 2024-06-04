package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Roznice_MainActivity : AppCompatActivity() {
    private lateinit var gridLayoutTop: GridLayout
    private lateinit var gridLayoutBottom: GridLayout
    private lateinit var modifiedImages: List<String>
    private var startTime: Long = 0
    private var takes = 20
    private var correctImagesCount = 0
    private var endTime: Long = 0
    private var penaltyTime: Long = 0
    private var clickedWrongImages = mutableSetOf<String>()
    private var points = 0
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roznice_gra)
        gridLayoutTop = findViewById(R.id.gridLayoutTop)
        gridLayoutBottom = findViewById(R.id.gridLayoutBottom)
        startTime = System.currentTimeMillis()
        firestore = FirebaseFirestore.getInstance()
        generateAndDisplayImages()
    }

    private fun generateAndDisplayImages() {
        val images = listOf(
            "roznice_ic_armata1",
            "roznice_ic_czaszka1",
            "roznice_ic_kotwica1",
            "roznice_ic_krab1",
            "roznice_ic_orka1",
            "roznice_ic_palma1",
            "roznice_ic_papuga1",
            "roznice_ic_raczek1",
            "roznice_ic_rafa1",
            "roznice_ic_ryba1",
            "roznice_ic_statek1",
            "roznice_ic_woda1",
            "roznice_ic_wyspa1",
            "roznice_ic_armata2",
            "roznice_ic_czaszka2",
            "roznice_ic_kotwica2",
            "roznice_ic_krab2",
            "roznice_ic_orka2",
            "roznice_ic_palma2",
            "roznice_ic_papuga2",
            "roznice_ic_raczek2",
            "roznice_ic_rafa2",
            "roznice_ic_ryba2",
            "roznice_ic_statek2",
            "roznice_ic_woda2",
            "roznice_ic_wyspa2"
        ) // Lista twoich obrazów
        val shuffledImages = images.shuffled() // Mieszanie obrazów

        // Generowanie pierwszego zestawu obrazów
        generateImagesSet(gridLayoutTop, shuffledImages.take(takes), isPreview = true, clickable = false)

        // Generowanie drugiego zestawu z trzema zmienionymi obrazami
        val modifiedImages = shuffledImages.take(takes).toMutableList()

        // Wybieranie losowych 3 obrazów do zamiany
        val toReplace = modifiedImages.shuffled().take(3)

        // Lista obrazów, które są dostępne do zamiany (nie zawierają się w pierwszym zestawie)
        val availableToReplaceWith = images.filterNot { modifiedImages.contains(it) }.shuffled()

        // Zastąpienie 3 obrazów innymi
        toReplace.forEachIndexed { index, image ->
            val replaceWithIndex =
                index % availableToReplaceWith.size // Zapobieganie IndexOutOfBoundsException
            modifiedImages[modifiedImages.indexOf(image)] = availableToReplaceWith[replaceWithIndex]
        }

        // Generowanie drugiego zestawu obrazów
        generateImagesSet(gridLayoutBottom, modifiedImages, isPreview = false)
        val modifiedImagesIndexes = toReplace.map { shuffledImages.indexOf(it) }
        this.modifiedImages = modifiedImagesIndexes.map { modifiedImages[it] }
    }

    // Przykładowa obsługa kliknięcia
    private fun onImageClick(imageView: ImageView, imageName: String, isCorrect: Boolean) {
        if (isCorrect) {
            imageView.setBackgroundResource(R.drawable.roznice_changed_image_border)
            correctImagesCount++
            if (correctImagesCount == 3) { // Założenie, że 3 to liczba poprawnych obrazków do znalezienia
                endTime = System.currentTimeMillis()
                val totalTime = endTime - startTime + penaltyTime
                showCompletionDialog(totalTime)
                correctImagesCount = 0 // Reset licznika dla nowej gry
                clickedWrongImages.clear() // Czyszczenie listy klikniętych błędnie obrazków
            }
        } else {
            if (!clickedWrongImages.contains(imageName)) { // Sprawdzenie, czy obrazek nie był kliknięty wcześniej
                imageView.setBackgroundResource(R.drawable.roznice_wrong_image_border)
                penaltyTime += 5000
                clickedWrongImages.add(imageName) // Dodanie obrazka do zestawu klikniętych błędnie
            }
            // Nie dodajemy karnego czasu, jeśli obrazek był już kliknięty jako błędny
        }
    }

    private fun showCompletionDialog(totalTime: Long) {
        val totalTimeSeconds = totalTime / 1000
        points = calculatePoints(totalTimeSeconds)
        savePointsToSharedPreferences("roznice_points", points)

        AlertDialog.Builder(this).apply {
            setTitle("Gra zakończona!")
            setMessage("Zdobyłeś: $points punktów")
            setNeutralButton("Następna gra") { dialog, which ->
                setResult(RESULT_OK) // Set the result to OK
                finish() // Ends current game to start the next one
            }
            setCancelable(false) // Zapobiega zamknięciu dialogu przez kliknięcie poza jego obszarem
            show()
        }
    }

    private fun calculatePoints(totalTimeSeconds: Long): Int {
        return when {
            totalTimeSeconds <= 10 -> 10
            totalTimeSeconds <= 12 -> 9
            totalTimeSeconds <= 14 -> 8
            totalTimeSeconds <= 16 -> 7
            totalTimeSeconds <= 18 -> 6
            totalTimeSeconds <= 20 -> 5
            totalTimeSeconds <= 22 -> 4
            totalTimeSeconds <= 24 -> 3
            totalTimeSeconds <= 26 -> 2
            totalTimeSeconds <= 28 -> 1
            else -> 0
        }
    }

    private fun savePointsToSharedPreferences(key: String, points: Int) {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, points)
        editor.apply()

        Log.d("Roznice_MainActivity", "Saved $points points to SharedPreferences with key $key")

        val username = sharedPreferences.getString("username", "Unknown User") ?: "Unknown User"
        savePointsToFirestore(username, points)
    }

    private fun savePointsToFirestore(username: String, points: Int) {
        val data = hashMapOf(
            "username" to username,
            "game" to "Roznice",
            "points" to points,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("points")
            .add(data)
            .addOnSuccessListener {
                Log.d("Roznice_MainActivity", "Points successfully written to Firestore for user $username")
            }
            .addOnFailureListener { e ->
                Log.e("Roznice_MainActivity", "Error writing points to Firestore", e)
            }
    }

    private fun restartGame() {
        correctImagesCount = 0
        startTime = System.currentTimeMillis()
        penaltyTime = 0
        generateAndDisplayImages()
    }

    private fun generateImagesSet(gridLayout: GridLayout, imageNames: List<String>, isPreview: Boolean, clickable: Boolean = true) {
        gridLayout.removeAllViews() // Usuwanie poprzednich obrazów
        val context: Context = gridLayout.context
        imageNames.forEach { imageName ->
            val imageView = ImageView(context).apply {
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                setBackgroundResource(R.drawable.roznice_image_border)

                val imgResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
                if (imgResId != 0) { // Jeżeli znaleziono zasób
                    setImageResource(imgResId)
                }

                // Ustaw mniejsze rozmiary dla podglądu
                val sizeVertical = 200 / (takes / 4) // Przykładowe wartości, dostosuj do potrzeb
                layoutParams = GridLayout.LayoutParams().apply {
                    if (!isPreview) {
                        width = GridLayout.LayoutParams.WRAP_CONTENT
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        setMargins(4, 4, 4, 4)
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    } else {
                        width = dpToPx(sizeVertical, context)
                        height = dpToPx(sizeVertical, context)
                        setMargins(2, 2, 2, 2)
                    }
                }

                if (clickable) {
                    setOnClickListener {
                        onImageClick(this, imageName, isImageCorrect(imageName))
                    }
                } else {
                    isClickable = false // Wyłączenie klikalności dla obrazków podglądu
                }
            }
            gridLayout.addView(imageView)
        }
    }

    private fun isImageCorrect(imageName: String): Boolean {
        return modifiedImages.contains(imageName)
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
