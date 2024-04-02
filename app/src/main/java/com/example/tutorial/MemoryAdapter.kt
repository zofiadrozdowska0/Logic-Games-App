package com.example.tutorial

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorial.models.BoardSize
import kotlin.math.min
import kotlin.random.Random

class MemoryAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val memoryGame: MemoryGame,
    private val rvBoard: RecyclerView,
    private val tvNumElements: TextView,
    private val tvText: TextView
) : RecyclerView.Adapter<MemoryAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
        private const val DELAY_BETWEEN_REVEALED_CARDS_MILLIS = 1000L
        private var currentSequenceIndex: Int = 0
    }
    private var sequence: List<Int>? = null
    private var isClickable = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams =
            view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
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

        fun bind(position: Int) {
            // You can implement any additional logic here if needed
            imageButton.setOnClickListener {
                if (isClickable) {
                    onCardClicked(position)

                }
            }
        }
    }

    private fun onCardClicked(position: Int) {

        if (isClickable) {
            if (position == sequence?.getOrNull(currentSequenceIndex)) {
                val viewHolder = rvBoard.findViewHolderForAdapterPosition(position) as ViewHolder
                viewHolder.imageButton.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_green_light
                    )
                )
                // Post a delayed action to revert the color after 0.5 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    viewHolder.imageButton.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.black // or the original color you want to set
                        )
                    )
                }, 150) // 500 milliseconds = 0.5 seconds


                Log.d(TAG, "Gracz kliknął poprawną kartę na pozycji: $position")
                currentSequenceIndex++

                if (currentSequenceIndex == sequence?.size) {
                    Log.d(TAG, "Gracz poprawnie ukończył sekwencję.")
                    memoryGame.level++
                    tvNumElements.text = "Level: ${memoryGame.level-1}" // Aktualizacja TextView z wartością level
                    currentSequenceIndex = 0
                    startGame()
                }
            } else {
                Toast.makeText(context, "Błędny wybór! Gra zostanie zresetowana.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Gracz kliknął nieprawidłową kartę na pozycji: $position")
                sequence=null
                currentSequenceIndex=0
                memoryGame.level = 2
                startGame()
            }
        }
    }




    val graphics_items = listOf(
        R.drawable.ic_armata2,
        R.drawable.ic_czaszka2,
        R.drawable.ic_kotwica2,
        R.drawable.ic_krab2,
        R.drawable.ic_orka2,
        R.drawable.ic_palma2,
        R.drawable.ic_papuga2,
        R.drawable.ic_raczek2,
        R.drawable.ic_rafa2,
        R.drawable.ic_ryba2,
        R.drawable.ic_statek2,
        R.drawable.ic_woda2,
        R.drawable.ic_wyspa2
    )
    fun startGame() {
        // Sprawdź, czy sekwencja została już wygenerowana
        sequence = memoryGame.getSequence()
        Log.d(TAG, "Wygenerowana sekwencja: $sequence")

        isClickable = false
        tvNumElements.text = "Level: ${memoryGame.level-1}" // Aktualizacja TextView z wartością level
        showSequence()


    }

    private fun showSequence() {
        val handler = Handler(Looper.getMainLooper())
        var nr_indeksu: Int
        var nr_indeksu_wyswietl: Int
        var lista = List(8) { 0 }
        handler.postDelayed({
            tvText.text = "SHOWING SEQUENCE..."
            sequence?.forEachIndexed { index, position ->
                handler.postDelayed({
                    val viewHolder = rvBoard.findViewHolderForAdapterPosition(position)
                    if (viewHolder != null && viewHolder is ViewHolder) {
                        viewHolder.imageButton.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_green_light
                            )
                        )

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewHolder.imageButton.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    android.R.color.black
                                )
                            )

                            Handler(Looper.getMainLooper()).postDelayed({
                                if (index == sequence!!.size - 1) {
                                    isClickable = true

                                    // Dodanie numeru indeksu
                                    nr_indeksu = (0 until sequence!!.size).random()
                                    nr_indeksu_wyswietl = nr_indeksu+1
                                    tvText.text = "YOUR SEQUENCE"
                                }
                            }, 100)

                        }, 300)

                    }
                }, index * DELAY_BETWEEN_REVEALED_CARDS_MILLIS)
            }
        }, 900)


    }

}
