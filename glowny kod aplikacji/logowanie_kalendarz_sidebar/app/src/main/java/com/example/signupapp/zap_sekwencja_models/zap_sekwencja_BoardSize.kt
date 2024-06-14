package com.example.signupapp

enum class zap_sekwencja_BoardSize(val numCards: Int){
    Easy(4),
    Medium(8),
    Hard(12);

    fun getWidth():Int{
        return when (this){
            Easy -> 1
            Medium -> 2
            Hard -> 3
        }
    }
    fun getHeight():Int{
        return numCards/getWidth()
    }

}