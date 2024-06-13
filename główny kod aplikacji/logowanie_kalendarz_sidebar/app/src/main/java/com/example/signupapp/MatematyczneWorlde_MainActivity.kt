package com.example.signupapp

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import android.os.Handler


class MatematyczneWorlde_MainActivity : AppCompatActivity() {
    public var score = 0
    private lateinit var equations: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matematyczneworlde_main)
        equations = loadEquationsFromAssets()
        val randomEquation = equations.random()
        val text11: TextView = findViewById(R.id.text11)
        val text12: TextView = findViewById(R.id.text12)
        val text13: TextView = findViewById(R.id.text13)
        val text14: TextView = findViewById(R.id.text14)
        val text15: TextView = findViewById(R.id.text15)
        val text16: TextView = findViewById(R.id.text16)
        val text17: TextView = findViewById(R.id.text17)
        val text18: TextView = findViewById(R.id.text18)
        val text21: TextView = findViewById(R.id.text21)
        val text22: TextView = findViewById(R.id.text22)
        val text23: TextView = findViewById(R.id.text23)
        val text24: TextView = findViewById(R.id.text24)
        val text25: TextView = findViewById(R.id.text25)
        val text26: TextView = findViewById(R.id.text26)
        val text27: TextView = findViewById(R.id.text27)
        val text28: TextView = findViewById(R.id.text28)
        val text31: TextView = findViewById(R.id.text31)
        val text32: TextView = findViewById(R.id.text32)
        val text33: TextView = findViewById(R.id.text33)
        val text34: TextView = findViewById(R.id.text34)
        val text35: TextView = findViewById(R.id.text35)
        val text36: TextView = findViewById(R.id.text36)
        val text37: TextView = findViewById(R.id.text37)
        val text38: TextView = findViewById(R.id.text38)
        val text41: TextView = findViewById(R.id.text41)
        val text42: TextView = findViewById(R.id.text42)
        val text43: TextView = findViewById(R.id.text43)
        val text44: TextView = findViewById(R.id.text44)
        val text45: TextView = findViewById(R.id.text45)
        val text46: TextView = findViewById(R.id.text46)
        val text47: TextView = findViewById(R.id.text47)
        val text48: TextView = findViewById(R.id.text48)
        val text51: TextView = findViewById(R.id.text51)
        val text52: TextView = findViewById(R.id.text52)
        val text53: TextView = findViewById(R.id.text53)
        val text54: TextView = findViewById(R.id.text54)
        val text55: TextView = findViewById(R.id.text55)
        val text56: TextView = findViewById(R.id.text56)
        val text57: TextView = findViewById(R.id.text57)
        val text58: TextView = findViewById(R.id.text58)
        val text61: TextView = findViewById(R.id.text61)
        val text62: TextView = findViewById(R.id.text62)
        val text63: TextView = findViewById(R.id.text63)
        val text64: TextView = findViewById(R.id.text64)
        val text65: TextView = findViewById(R.id.text65)
        val text66: TextView = findViewById(R.id.text66)
        val text67: TextView = findViewById(R.id.text67)
        val text68: TextView = findViewById(R.id.text68)

        val button0: Button = findViewById(R.id.button0)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)
        val button10: Button = findViewById(R.id.button10)
        val button11: Button = findViewById(R.id.button11)
        val button12: Button = findViewById(R.id.button12)
        val button13: Button = findViewById(R.id.button13)
        val button14: Button = findViewById(R.id.button14)
        val button15: Button = findViewById(R.id.button15)  //enter
        val button16: Button = findViewById(R.id.button16) //delete
        var currentListIndex = 0
        val listOfTextViews = listOf(
            listOf(
                text11,
                text12,
                text13,
                text14,
                text15,
                text16,
                text17,
                text18
                ),
            listOf(
                text21,
                text22,
                text23,
                text24,
                text25,
                text26,
                text27,
                text28
            ),
            listOf(
                text31,
                text32,
                text33,
                text34,
                text35,
                text36,
                text37,
                text38
            ),
            listOf(
                text41,
                text42,
                text43,
                text44,
                text45,
                text46,
                text47,
                text48
            ),
            listOf(
                text51,
                text52,
                text53,
                text54,
                text55,
                text56,
                text57,
                text58
            ),
            listOf(
                text61,
                text62,
                text63,
                text64,
                text65,
                text66,
                text67,
                text68
            ),
        )
        val listOfButtons = listOf(
            button0,
            button1,
            button2,
            button3,
            button4,
            button5,
            button6,
            button7,
            button8,
            button9,
            button10,
            button11,
            button12,
            button13,
            button14,
            button15,
            button16
        )

        //wpisywanie
        listOfButtons.forEach { button ->
            button.setOnClickListener {
                val currentList = listOfTextViews[currentListIndex]
                for (text in currentList) {
                    if (text.text.isBlank()) {
                        text.text = button.text
                        return@setOnClickListener // Przerywamy pętlę, gdy znajdziemy pusty TextView
                    }
                }
            }
        }
        fun disableButtons() {
            for (button in listOfButtons) {
                button.isEnabled = false
            }
        }
        fun isEquationValid(input: String): Boolean {
            var hasOperator = false
            var hasEqualsSign = false
            var lastCharWasDigit = false

            for (char in input) {
                if (char.isDigit()) {
                    lastCharWasDigit = true
                } else if (char in setOf('+', '-', '*', '/')) {
                    if (!lastCharWasDigit) return false // Operator cannot be directly followed by another operator
                    hasOperator = true
                    lastCharWasDigit = false
                } else if (char == '=') {
                    if (!lastCharWasDigit || !hasOperator || hasEqualsSign) return false
                    hasEqualsSign = true
                    lastCharWasDigit = false
                }
            }

            return hasOperator && hasEqualsSign && lastCharWasDigit
        }
        fun evaluateExpression(expression: String): Double {
            val operators = listOf('+', '-', '*', '/')
            val numStack = mutableListOf<Double>()
            val opStack = mutableListOf<Char>()
            val numBuffer = StringBuilder()

            fun popAndApply() {
                val num2 = numStack.removeLast()
                val num1 = numStack.removeLast()
                val op = opStack.removeLast()
                val result = when (op) {
                    '+' -> num1 + num2
                    '-' -> num1 - num2
                    '*' -> num1 * num2
                    '/' -> num1 / num2
                    else -> throw IllegalArgumentException("Invalid operator")
                }
                numStack.add(result)
            }

            for (char in expression) {
                if (char.isDigit() || char == '.') {
                    numBuffer.append(char)
                } else if (char in operators) {
                    if (numBuffer.isNotEmpty()) {
                        numStack.add(numBuffer.toString().toDouble())
                        numBuffer.clear()
                    }
                    while (opStack.isNotEmpty() && operators.indexOf(opStack.last()) >= operators.indexOf(char)) {
                        popAndApply()
                    }
                    opStack.add(char)
                }
            }
            if (numBuffer.isNotEmpty()) {
                numStack.add(numBuffer.toString().toDouble())
            }
            while (opStack.isNotEmpty()) {
                popAndApply()
            }
            return numStack.first()
        }

// Nowa linia
        button15.setOnClickListener {
            var equationString = ""
            val checklist = listOfTextViews[currentListIndex]


            for (text in checklist) {
                equationString += text.text // Append the text content of each TextView
            }

            if (isEquationValid(equationString)) {
                val sides = equationString.split("=")
                val leftSideValue = evaluateExpression(sides[0])
                val rightSideValue = evaluateExpression(sides[1])

                if (leftSideValue == rightSideValue) {
                    compareEquations(equationString, randomEquation, listOfTextViews[currentListIndex])
                    if (checkRowForWin(listOfTextViews[currentListIndex])) {
                        Toast.makeText(this@MatematyczneWorlde_MainActivity, "Gratulacje, wygrałeś!", Toast.LENGTH_SHORT).show()
                        if (currentListIndex == 2){
                            score=8
                        }
                        else if (currentListIndex==3){
                            score=6
                        }
                        else if (currentListIndex==4){
                            score=4
                        }
                        else if (currentListIndex==5){
                            score=2
                        }
                        else{
                            score=10
                        }
                        disableButtons()
                        Handler().postDelayed({
                            savePointsToSharedPreferences("pierwsza", score)
                            val intent = Intent(applicationContext, saper_minefield::class.java)
                            startActivity(intent)
                        }, 1000)

                    } else if (currentListIndex == 5) {
                        // Jeśli to ostatni rząd i nie ma zwycięstwa, wyświetl informację o porażce i poprawne równanie
                        Toast.makeText(this@MatematyczneWorlde_MainActivity, "Niestety przegrałeś. Równanie, którego szukałeś to: $randomEquation", Toast.LENGTH_LONG).show()
                        score=0
                        disableButtons()
                        Handler().postDelayed({
                            savePointsToSharedPreferences("pierwsza", score)
                            val intent = Intent(applicationContext, saper_minefield::class.java)
                            startActivity(intent)
                        }, 1000)
                    } else {
                        currentListIndex = (currentListIndex + 1) % listOfTextViews.size // Przejdź do następnego rzędu
                    }
                } else {
                    // Show the in-game pop-up indicating the equation is invalid
                    Toast.makeText(this@MatematyczneWorlde_MainActivity, "Invalid equation: $equationString", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show the in-game pop-up indicating the equation is invalid
                Toast.makeText(this@MatematyczneWorlde_MainActivity, "Invalid equation: $equationString", Toast.LENGTH_SHORT).show()
            }
        }
        //kasowanie
        button16.setOnClickListener {
            val currentList = listOfTextViews[currentListIndex]
            for (i in currentList.size - 1 downTo 0) {
                val textView = currentList[i]
                if (textView.text.isNotBlank()) {
                    textView.text = "" // Czyszczenie ostatniego TextView
                    return@setOnClickListener // Przerywamy pętlę po znalezieniu i wyczyszczeniu ostatniego niepustego TextView
                }
            }
        }
    }
    private fun savePointsToSharedPreferences(key: String, points: Int) {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, points)
        editor.apply()
    }
    private fun loadEquationsFromAssets(): List<String> {
        val equationsList = mutableListOf<String>()
        val assetManager: AssetManager = applicationContext.assets
        try {
            val inputStream = assetManager.open("equations.txt")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                equationsList.add(line)
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return equationsList
    }
    private fun compareEquations(userEquation: String, randomEquation: String, textViewList: List<TextView>) {
        val maxEquationLength = maxOf(userEquation.length, randomEquation.length)

        for (i in 0 until maxEquationLength) {
            val userChar = if (i < userEquation.length) userEquation[i] else ' '
            val randomChar = if (i < randomEquation.length) randomEquation[i] else ' '

            val colorResId = when {
                userChar == ' ' -> R.color.red // Nie ma znaku wpisanego przez użytkownika
                userChar == randomChar -> R.color.green // Znak jest na właściwym miejscu
                randomEquation.contains(userChar) -> R.color.yellow // Znak występuje w równaniu, ale na innym miejscu
                else -> R.color.red // Znak nie występuje w równaniu
            }

            val textView = textViewList[i] // Pobierz odpowiedni TextView z listy

            // Zapisz aktualny kolor tła jako atrybut TextView
            textView.setTag(R.id.colorTag, colorResId)

            // Sprawdź, czy kolor tła TextView ma być zmieniony
            val currentColor = textView.getTag(R.id.colorTag) as Int
            if (resources.getColor(colorResId, null) != currentColor) {
                // Zastosuj animację tylko w przypadku zmiany koloru
                val rotateAnimation = AnimationUtils.loadAnimation(this@MatematyczneWorlde_MainActivity, R.anim.rotate_animation)
                rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        textView.setBackgroundResource(colorResId)
                        textView.setTag(R.id.colorTag, colorResId) // Zaktualizuj atrybut z kolorem tła
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                textView.startAnimation(rotateAnimation)
            }
        }
    }

    private fun checkRowForWin(textViewList: List<TextView>): Boolean {
        for (textView in textViewList) {
            val colorResId = textView.getTag(R.id.colorTag) as Int
            if (colorResId != R.color.green) {
                return false // Jeśli jakiekolwiek pole nie jest zielone, zwróć false
            }
        }
        return true // Jeśli wszystkie pola są zielone, zwróć true
    }
}
