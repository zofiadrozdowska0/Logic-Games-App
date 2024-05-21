package com.example.signupapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
class LineChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var dataPointsList: List<List<Pair<Float, Float>>> = emptyList()

    fun setDataPointsList(dataPointsList: List<List<Pair<Float, Float>>>) {
        this.dataPointsList = dataPointsList
        println("gwozd" + dataPointsList)
        invalidate() // Odświeżamy widok, aby narysować nowe dane
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.let {
            drawAxes(it)
            drawDataPoints(it)
        }
    }

    private fun drawAxes(canvas: Canvas) {
        // Rysujemy osie wykresu
        val paint = Paint()
        paint.color = resources.getColor(android.R.color.black)
        paint.strokeWidth = 4f // Zwiększamy grubość linii na 4 piksele

        // Przesuwamy osie o 50 pikseli w dół
        val axisOffset = 50f

        // Oś X
        val xAxisYPosition = height.toFloat() - 200f + axisOffset // Opuszczamy osie o 50 pikseli i przesuwamy o 50 pikseli w dół
        canvas.drawLine(100f, xAxisYPosition, width.toFloat() - 100f, xAxisYPosition, paint)

        // Oś Y
        val yAxisXPosition = 100f // Pozostawiamy oś Y na tej samej wysokości
        canvas.drawLine(yAxisXPosition, height.toFloat() - 100f - axisOffset, yAxisXPosition, 100f, paint)

        // Podziałki na osi X
        val xTickCount = 7 // Liczba podziałek na osi X
        val xTickSpacing = (width - 200) / (xTickCount - 1).toFloat()

        // Pobieramy daty z ostatnich 7 dni z tabeli PointsHistory
        val dates = getDatesFromLast7Days()

        // Zmiana rozmiaru czcionki na 30 pikseli
        paint.textSize = 30f

        // Rysujemy podziałki na osi X z datami
        for (i in 0 until xTickCount) {
            val x = 100f + i * xTickSpacing
            canvas.drawText(dates[i], x - 20, xAxisYPosition + 50, paint) // Dodajemy margines dla dat
            canvas.drawLine(x, xAxisYPosition - 5, x, xAxisYPosition + 5, paint)
        }

        // Podziałki na osi Y
        val yTickCount = 11 // Liczba podziałek na osi Y
        val yTickSpacing = (height - 250) / (yTickCount - 1).toFloat()

        // Zmiana rozmiaru czcionki na 40 pikseli
        paint.textSize = 40f

        for (i in 0 until yTickCount) {
            val y = height.toFloat() - 100f - i * yTickSpacing - axisOffset // Przesuwamy o 50 pikseli w dół
            canvas.drawText(i.toString(), yAxisXPosition - 50, y + 15, paint) // Przesuwamy tekst nieco w lewo
            canvas.drawLine(yAxisXPosition - 5, y, yAxisXPosition + 5, y, paint)
        }
    }


    private fun drawDataPoints(canvas: Canvas) {
        // Rysujemy punkty danych na wykresie oraz linie łączące punkty tego samego koloru
        val paintList = listOf(
            Paint().apply { color = resources.getColor(android.R.color.holo_blue_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_red_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_green_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_orange_dark) }
        )

        val radius = 8f

        for ((index, points) in dataPointsList.withIndex()) {
            val paint = paintList[index % paintList.size]

            var lastX: Float? = null
            var lastY: Float? = null

            for (point in points) {
                // Pobieramy rzeczywiste współrzędne punktu na wykresie
                val x = 100f + point.first * ((width - 200) / 6) // Współrzędna X
                val y = height.toFloat() - 150f - point.second * ((height - 250) / 10) // Współrzędna Y

                // Sprawdzamy czy punkt nie znajduje się na osiach X lub Y
                if (!(x < 100f || x > width.toFloat() - 100f || y < 100f || y > height.toFloat() - 150f)) {
                    canvas.drawCircle(x, y, radius, paint)

                    // Rysujemy linię łączącą punkty tego samego koloru tylko jeśli oba punkty są widoczne na wykresie
                    if (lastX != null && lastY != null && paint.color == paintList[index % paintList.size].color) {
                        canvas.drawLine(lastX, lastY, x, y, paint)
                    }
                }

                lastX = x
                lastY = y
            }
        }
    }





    private fun getDatesFromLast7Days(): List<String> {
        val dates = ArrayList<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())

        for (i in 0 downTo -6) { // Pobieramy daty od dzisiaj do 6 dni wstecz
            calendar.add(Calendar.DAY_OF_YEAR, i)
            dates.add(dateFormat.format(calendar.time))
            calendar.time = Date() // Przywracamy bieżącą datę
        }

        return dates.reversed() // Odwracamy listę, aby daty były w kolejności od najstarszej do najnowszej
    }
}
