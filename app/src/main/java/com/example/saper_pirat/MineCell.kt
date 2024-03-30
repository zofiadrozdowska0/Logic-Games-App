package com.example.saper_pirat

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MineCell @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    var value: Int = 0
    var isRevealed: Boolean = false
    var isFlagged: Boolean = false
    var hasBomb: Boolean = false

    // Metoda do ustawienia statusu pola na odkryte
    fun setRevealed() {
        isRevealed = true
    }

    // Metoda do ustawienia statusu pola na oznaczone flagą lub odznaczone flagą
    fun setFlaggedStatus(flagged: Boolean) {
        isFlagged = flagged
    }

}
