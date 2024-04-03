package com.example.remembersequence

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.remembersequence.models.BoardSize
import kotlin.math.min
import android.graphics.Color
import android.util.Log
import android.widget.Toast

class MemoryBoard(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cardImages: List<Int>,
    private val rvBoard: RecyclerView
) :
    RecyclerView.Adapter<MemoryBoard.ViewHolder>() {

    companion object {
        private const val TAG = "MemoryBoard"
        private const val MARGIN_SIZE = 10
        private const val DELAY_BEFORE_HIDING_UPPER_CARDS = 5000L // 5 seconds
    }

    private val revealedCards = MutableList(boardSize.numCards) { false }
    private var isClickable = false
    private var sequence = generateRandomSequence()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 2 - 2 * MARGIN_SIZE
        val cardHeight = parent.height / 4 - 2 * MARGIN_SIZE
        val cardSideLength = min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.activity_memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun getItemCount() = boardSize.numCards

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        var pozycja: Int = -1
        var tlo: List<Int> = emptyList()
        var clickedPosition = -1

        init {
            updateImagesForSecondHalf()
        }

        private fun updateImagesForSecondHalf() {
            val (tloList, nonMatchingPosition) = generateRandomImagesForSecondHalf()
            pozycja = nonMatchingPosition
            tlo = tloList
        }

        fun bind(position: Int) {
            clickedPosition = position

            if (sequence != null) {
                if (position < sequence.size) {
                    // Ustawienie obrazków na pierwszej połowie kart
                    imageButton.setImageResource(sequence[position])
                } else {
                    // Ustawienie obrazków na drugiej połowie kart
                    val secondHalfPosition = position - sequence.size
                    imageButton.setImageResource(R.drawable.ic_launcher_background)
                }

                // Opóźnienie zmiany obrazka karty po 5 sekundach
                Handler(Looper.getMainLooper()).postDelayed({
                    isClickable = true
                    if (clickedPosition >= sequence!!.size) {
                        // Ustawienie obrazków na drugiej połowie kart takich samych jak na pierwszej połowie
                        val secondHalfPosition = clickedPosition - sequence!!.size
                        imageButton.setImageResource(tlo[secondHalfPosition])
                    } else {
                        // Ustawienie obrazka na tło po 5 sekundach na pierwszej połowie kart
                        imageButton.setImageResource(R.drawable.ic_launcher_background)
                    }
                }, DELAY_BEFORE_HIDING_UPPER_CARDS)
            } else {
                imageButton.setImageResource(R.drawable.ic_launcher_background)
            }

            imageButton.setOnClickListener {
                if (isClickable) {
                    if (clickedPosition >= sequence.size && clickedPosition < boardSize.numCards) {
                        // Kliknięcie na kartę w drugiej połowie (odpowiedź)
                        val a = pozycja
                        Log.d(TAG, "Clicked position: $clickedPosition")
                        Log.d(TAG,"$a")
                        if (clickedPosition - sequence.size == a) {
                            // Kliknięta karta jest na pozycji nonMatchingPosition
                            showToast("Masz punkt!")
                            restartGame()
                        } else {
                            showToast("Błąd!")
                            restartGame()
                        }
                    } else {
                        // Kliknięcie na kartę w pierwszej połowie
                        // Tutaj możesz dodać logikę, jeśli chcesz obsłużyć kliknięcie na karty w pierwszej połowie
                    }
                    // Logowanie pozycji
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    private fun generateRandomSequence(): List<Int> {
        return cardImages.shuffled().distinct().take(4)
    }

    private fun generateRandomImagesForSecondHalf(): Pair<List<Int>, Int> {
        val sequenceElements = sequence ?: return Pair(emptyList(), -1)
        val nonSequenceImages = cardImages.distinct().filter { it !in sequenceElements }

        val randomSequenceImages = mutableListOf<Int>()
        while (randomSequenceImages.size < 3) {
            val randomElement = sequenceElements.shuffled().firstOrNull()
            if (randomElement != null && randomElement !in randomSequenceImages) {
                randomSequenceImages.add(randomElement)
            }
        }

        // Losujemy jeden obraz spoza sekwencji
        val randomNonSequenceImage = nonSequenceImages.randomOrNull()

        // Zapisujemy pozycję niepasującego elementu
        val nonMatchingPosition = randomSequenceImages.size

        // Tworzymy listę obrazków na drugiej połowie karty
        val secondHalfImages = mutableListOf<Int>()
        secondHalfImages.addAll(randomSequenceImages)
        randomNonSequenceImage?.let { secondHalfImages.add(it) }

        // Tasujemy kolejność kart na dole
        return Pair(secondHalfImages.shuffled(), nonMatchingPosition)
    }

    private fun restartGame() {
        sequence = generateRandomSequence()

        notifyDataSetChanged()
    }
}



