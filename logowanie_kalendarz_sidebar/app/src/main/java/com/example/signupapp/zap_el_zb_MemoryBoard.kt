package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min
import android.util.Log
import android.widget.Toast
import android.widget.TextView
class zap_el_zb_MemoryBoard(
    private val context: Context,
    private val boardSize: zap_el_zb_BoardSize,
    private val cardImages: List<Int>,
    private val tvNumMoves: TextView,
    private val tvNumPairs: TextView

) : RecyclerView.Adapter<zap_el_zb_MemoryBoard.ViewHolder>() {

    companion object {
        private const val TAG = "MemoryBoard"
        private const val MARGIN_SIZE = 10
        private const val DELAY_BEFORE_HIDING_UPPER_CARDS = 5000L // 5 seconds
    }

    private var isClickable = false
    private var sequence: List<Int> = generateRandomSequence()
    private var secondHalfImages: Pair<List<Int>, Int> = generateRandomImagesForSecondHalf(sequence)
    private var points_el_zb =0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 2 - 2 * MARGIN_SIZE
        val cardHeight = parent.height / 4 - 2 * MARGIN_SIZE
        val cardSideLength = min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.zap_el_zb_activity_memory_card, parent, false)
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
        private val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)

        fun bind(position: Int) {
            // Display the initial sequence of images
            if (position < sequence.size) {
                imageButton.setImageResource(sequence[position])
            } else {
                imageButton.setImageResource(R.drawable.zap_el_zb_ic_launcher_background)
            }
            tvNumMoves.text = "Points: ${points_el_zb}"
            tvNumPairs.text = "Your Sequence"

            // Hide images after delay
            Handler(Looper.getMainLooper()).postDelayed({
                tvNumPairs.text = "Your Answer"
                isClickable = true
                if (position >= sequence.size) {
                    val secondHalfPosition = position - sequence.size
                    imageButton.setImageResource(secondHalfImages.first[secondHalfPosition])
                } else {
                    imageButton.setImageResource(R.drawable.zap_el_zb_ic_launcher_background)
                }
            }, DELAY_BEFORE_HIDING_UPPER_CARDS)
            tvNumPairs.text = "Your Sequence"
            // Set click listener for second half images
            imageButton.setOnClickListener {
                if (isClickable && position >= sequence.size) {
                    val secondHalfPosition = position - sequence.size
                    val clickedImage = secondHalfImages.first[secondHalfPosition]
                    val correctImage = secondHalfImages.first[secondHalfImages.second]

                    if (clickedImage == correctImage) {
                        showToast("Good Answer!")
                        points_el_zb++
                        if (points_el_zb<10){
                            tvNumMoves.text = "Points: ${points_el_zb}"
                        }
                        else{
                            showToast("Maximum points!")
                            tvNumMoves.text = "Points: ${points_el_zb}"
                            points_el_zb = 0
                            val intent = Intent(context, wybor_gry::class.java)
                            context.startActivity(intent)
                        }


                    } else {
                        showToast("Wrong Answer!")
                        points_el_zb = 0
                        tvNumMoves.text = "Points: ${points_el_zb}"
                        val intent = Intent(context, wybor_gry::class.java)
                        context.startActivity(intent)
                    }
                    restartGame()
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

    private fun generateRandomImagesForSecondHalf(sequence: List<Int>): Pair<List<Int>, Int> {
        var secondHalfImages = sequence.shuffled().toMutableList()
        val nonSequenceImages = cardImages.filter { it !in sequence }
        if (nonSequenceImages.isNotEmpty()) {
            val randomNonSequenceImage = nonSequenceImages.random()
            val randomIndex = (0 until secondHalfImages.size).random() // Losowy indeks od 0 do wielkości listy
            secondHalfImages[randomIndex] = randomNonSequenceImage
            return Pair(secondHalfImages, randomIndex)
        } else {
            //Log.e(TAG, "Brak dostępnych obrazków spoza sekwencji")
            return Pair(secondHalfImages.shuffled(), secondHalfImages.lastIndex)
        }
    }

    private fun restartGame() {
        sequence = generateRandomSequence()
        secondHalfImages = generateRandomImagesForSecondHalf(sequence)
        notifyDataSetChanged()
    }
}
