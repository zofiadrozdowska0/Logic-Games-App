package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Roznice_MainActivity : AppCompatActivity() {
    private lateinit var gridLayoutTop: GridLayout
    private lateinit var gridLayoutBottom: GridLayout
    private lateinit var modifiedImages: List<String>
    private var startTime: Long = 0
    private var takes = 20
    private var correctImagesCount = 0
    private var endTime: Long = 0
    private var penaltytime: Long = 0
    private var clickedWrongImages = mutableSetOf<String>()
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roznice_gra)
        gridLayoutTop = findViewById(R.id.gridLayoutTop)
        gridLayoutBottom = findViewById(R.id.gridLayoutBottom)
        startTime = System.currentTimeMillis()
        generateAndDisplayImages()
    }

    fun generateAndDisplayImages() {
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
    fun onImageClick(imageView: ImageView, imageName: String, isCorrect: Boolean) {
        if (isCorrect) {
            imageView.setBackgroundResource(R.drawable.roznice_changed_image_border)
            correctImagesCount++
            if (correctImagesCount == 3) { // Założenie, że 3 to liczba poprawnych obrazków do znalezienia
                endTime = System.currentTimeMillis()
                val totalTime = endTime - startTime + penaltytime
                showCompletionDialog(totalTime)
                correctImagesCount = 0 // Reset licznika dla nowej gry
                clickedWrongImages.clear() // Czyszczenie listy klikniętych błędnie obrazków
            }
        } else {
            if (!clickedWrongImages.contains(imageName)) { // Sprawdzenie, czy obrazek nie był kliknięty wcześniej
                imageView.setBackgroundResource(R.drawable.roznice_wrong_image_border)
                penaltytime += 5000
                clickedWrongImages.add(imageName) // Dodanie obrazka do zestawu klikniętych błędnie
            }
            // Nie dodajemy karnego czasu, jeśli obrazek był już kliknięty jako błędny
        }
    }

    fun showCompletionDialog(totalTime: Long) {
        val totalTimeSeconds = totalTime / 1000
        if (totalTimeSeconds <= 10) {
            points = 10
        } else if (totalTimeSeconds <= 12) {
            points = 9
        } else if (totalTimeSeconds <= 14) {
            points = 8
        } else if (totalTimeSeconds <= 16) {
            points = 7
        } else if (totalTimeSeconds <= 18) {
            points = 6
        } else if (totalTimeSeconds <= 20) {
            points = 5
        } else if (totalTimeSeconds <= 22) {
            points = 4
        } else if (totalTimeSeconds <= 24) {
            points = 3
        } else if (totalTimeSeconds <= 26) {
            points = 2
        } else if (totalTimeSeconds <= 28) {
            points = 1
        } else if (totalTimeSeconds > 28) {
            points = 0
        }
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


    fun restartGame() {
        correctImagesCount = 0
        startTime = System.currentTimeMillis()
        penaltytime = 0
        generateAndDisplayImages()
    }

    // Funkcja do dodawania obrazów do GridLayout
    fun generateImagesSet(gridLayout: GridLayout, imageNames: List<String>, isPreview: Boolean, clickable: Boolean = true) {
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
                val size_vertical = 200 / (takes / 4) // Przykładowe wartości, dostosuj do potrzeb
                layoutParams = GridLayout.LayoutParams().apply {
                    if (!isPreview) {
                        width = GridLayout.LayoutParams.WRAP_CONTENT
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        rightMargin = 4 // Ustaw marginesy zgodnie z potrzebami
                        leftMargin = 4
                        topMargin = 4
                        bottomMargin = 4
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    } else {
                        width = dpToPx(size_vertical, context)
                        height = dpToPx(size_vertical, context)
                        rightMargin = 2
                        leftMargin = 2
                        topMargin = 2
                        bottomMargin = 2
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

    fun isImageCorrect(imageName: String): Boolean {
        return modifiedImages.contains(imageName)
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
