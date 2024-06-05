package com.example.signupapp

import android.content.Context
import android.database.CursorWindowAllocationException
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LineChartView2(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var dataPointsList: List<List<Pair<Float, Float>>> = emptyList()
    private var username: String? = null


    fun setDataPointsList(dataPointsList: List<List<Pair<Float, Float>>>) {
        this.dataPointsList = dataPointsList
        invalidate() // Refresh the view to draw new data
    }


    fun fetchDataPointsFromDatabase(username: String) {

        val db = FirebaseFirestore.getInstance()
        db.collection("points")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                println("Successfully fetched points for user: $username")
                val reflexPointsMap = mutableMapOf<String, MutableList<Pair<Date, Float>>>()
                val perceptivenessPointsMap = mutableMapOf<String, MutableList<Pair<Date, Float>>>()
                val memoryPointsMap = mutableMapOf<String, MutableList<Pair<Date, Float>>>()
                val logicPointsMap = mutableMapOf<String, MutableList<Pair<Date, Float>>>()

                for (document in result) {
                    val date = document.getTimestamp("date")
                    val reflexPointsValue = document.getLong("reflex_points")?.toFloat() ?: 0f
                    val perceptivenessPointsValue = document.getLong("perceptiveness_points")?.toFloat() ?: 0f
                    val memoryPointsValue = document.getLong("memory_points")?.toFloat() ?: 0f
                    val logicPointsValue = document.getLong("logic_points")?.toFloat() ?: 0f

                    date?.let {
                        val dateObj = date.toDate()
                        val formattedDate = formatDate(dateObj)

                        updatePointsMap(reflexPointsMap, formattedDate, dateObj, reflexPointsValue)
                        updatePointsMap(perceptivenessPointsMap, formattedDate, dateObj, perceptivenessPointsValue)
                        updatePointsMap(memoryPointsMap, formattedDate, dateObj, memoryPointsValue)
                        updatePointsMap(logicPointsMap, formattedDate, dateObj, logicPointsValue)
                    }
                }

                // Print points for each category
                println("Reflex:")
                reflexPointsMap.forEach { (date, pointsList) ->
                    pointsList.forEach { points ->
                        println("Date: $date, Points: ${points.second}")
                    }
                }

                println("Spostrzegawczość:")
                perceptivenessPointsMap.forEach { (date, pointsList) ->
                    pointsList.forEach { points ->
                        println("Date: $date, Points: ${points.second}")
                    }
                }

                println("Pamięć:")
                memoryPointsMap.forEach { (date, pointsList) ->
                    pointsList.forEach { points ->
                        println("Date: $date, Points: ${points.second}")
                    }
                }

                println("Logic Points:")
                logicPointsMap.forEach { (date, pointsList) ->
                    pointsList.forEach { points ->
                        println("Date: $date, Points: ${points.second}")
                    }
                }

                val reflexPoints = mapToPointsList(reflexPointsMap)
                val perceptivenessPoints = mapToPointsList(perceptivenessPointsMap)
                val memoryPoints = mapToPointsList(memoryPointsMap)
                val logicPoints = mapToPointsList(logicPointsMap)

                setDataPointsList(listOf(reflexPoints, perceptivenessPoints, memoryPoints, logicPoints))
            }
            .addOnFailureListener { exception ->
                println("Error fetching points for user $username: $exception")
            }
    }


    private fun updatePointsMap(
        pointsMap: MutableMap<String, MutableList<Pair<Date, Float>>>,
        formattedDate: String,
        dateObj: Date,
        pointsValue: Float,
    ) {
        val currentEntries = pointsMap[formattedDate]

        if (currentEntries == null) {
            // Brak wpisów dla tej daty, tworzymy nową listę i dodajemy nową parę wartości
            pointsMap[formattedDate] = mutableListOf(Pair(dateObj, pointsValue / 3))
        } else {
            // Jeśli są już wpisy dla tej daty i kategorii
            currentEntries.add(Pair(dateObj, pointsValue)) // Dodajemy nową parę
        }
    }


    private fun mapToPointsList(pointsMap: Map<String, List<Pair<Date, Float>>>): List<Pair<Float, Float>> {
        val pointsList = mutableListOf<Pair<Float, Float>>()
        val dates = getDatesFromLast7Days()

        for (date in dates) {
            val points = pointsMap[date]
            if (points != null && points.isNotEmpty()) {
                val averagePoints = points.map { it.second }.average().toFloat()
                val dayIndex = getDayIndex(date)
                pointsList.add(Pair(dayIndex.toFloat(), averagePoints))
            } else {
                // Jeśli nie ma punktów dla danego dnia, dodajemy pustą parę
                val dayIndex = getDayIndex(date)
                pointsList.add(Pair(dayIndex.toFloat(), 0f))
            }
        }

        return pointsList
    }


    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getDayIndex(formattedDate: String): Int {
        val dates = getDatesFromLast7Days()
        return dates.indexOf(formattedDate)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.let {
            drawAxes(it)
            drawDataPoints(it)
            drawLegend(it)
        }
    }

    private fun drawAxes(canvas: Canvas) {
        // Drawing the chart axes
        val paint = Paint()
        paint.color = resources.getColor(android.R.color.black)
        paint.strokeWidth = 4f

        val axisOffset = 50f

        val xAxisYPosition = height.toFloat() - 300f + axisOffset
        canvas.drawLine(100f, xAxisYPosition, width.toFloat() - 100f, xAxisYPosition, paint)

        val yAxisXPosition = 100f
        canvas.drawLine(yAxisXPosition, height.toFloat() - 200f - axisOffset, yAxisXPosition, 100f, paint)

        val xTickCount = 7
        val xTickSpacing = (width - 200) / (xTickCount - 1).toFloat()

        val dates = getDatesFromLast7Days()

        paint.textSize = 30f

        for (i in 0 until xTickCount) {
            val x = 100f + i * xTickSpacing
            canvas.drawText(dates[i], x - 20, xAxisYPosition + 50, paint)
            canvas.drawLine(x, xAxisYPosition - 5, x, xAxisYPosition + 5, paint)
        }

        val yTickCount = 11
        val yTickSpacing = (height - 350) / (yTickCount - 1).toFloat()

        paint.textSize = 40f

        for (i in 0 until yTickCount) {
            val y = height.toFloat() - 200f - i * yTickSpacing - axisOffset
            canvas.drawText(i.toString(), yAxisXPosition - 50, y + 15, paint)
            canvas.drawLine(yAxisXPosition - 5, y, yAxisXPosition + 5, y, paint)
        }
    }

    private fun drawDataPoints(canvas: Canvas) {
        val radius = 10f
        val paintList = listOf(
            Paint().apply { color = resources.getColor(android.R.color.holo_blue_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_red_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_green_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_orange_dark) }
        )
        for ((index, points) in dataPointsList.withIndex()) {
            val paint = paintList[index % paintList.size]

            // Ustawienie odpowiedniego promienia punktów

            paint.strokeWidth = 10f // Ustawienie grubości linii

            var lastX: Float? = null
            var lastY: Float? = null

            for (point in points) {
                val x = 100f + point.first * ((width - 200) / 6)
                val y = height.toFloat() - 250f - point.second * ((height - 350) / 10) // Adjusted y by subtracting 50

                if (!(x < 100f || x > width.toFloat() - 100f || y < 100f || y > height.toFloat() - 250f)) {
                    canvas.drawCircle(x, y, radius, paint)

                    if (lastX != null && lastY != null && paint.color == paintList[index % paintList.size].color) {
                        canvas.drawLine(lastX, lastY, x, y, paint)
                    }
                }

                lastX = x
                lastY = y
            }
        }
    }


    private fun drawLegend(canvas: Canvas) {
        val paintList = listOf(
            Paint().apply { color = resources.getColor(android.R.color.holo_blue_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_red_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_green_dark) },
            Paint().apply { color = resources.getColor(android.R.color.holo_orange_dark) }
        )

        val labels = listOf("Reflex", "Spostrzegawczość", "Pamięć", "Logika")

        val paint = Paint()
        paint.textSize = 40f

        val legendBoxSize = 30f
        val legendSpacing = 20f
        val columnSpacing = 50f
        val legendMargin = 100f
        val legendYStart = height.toFloat() - 150 // Start position of the legend Y coordinate

        for (i in labels.indices) {
            val column = i % 2
            val row = i / 2

            var textOffset = 0f
            var colorBoxXOffset = 0f

            if (labels[i] == "Perceptiveness Points") {
                textOffset = -10f // Move "Perceptiveness Points" label to the left
                colorBoxXOffset = -150f // Move its color box to the left
            } else if (labels[i] == "Logic Points") {
                textOffset = -10f // Move "Logic Points" label to the right
                colorBoxXOffset = 30f // Move its color box to the right
            }

            val colorBoxX = legendMargin + column * (legendBoxSize + legendSpacing + columnSpacing + paint.measureText(labels[i])) + colorBoxXOffset
            val colorBoxY = legendYStart + row * (legendBoxSize + legendSpacing)

            paint.color = paintList[i].color
            canvas.drawRect(colorBoxX, colorBoxY, colorBoxX + legendBoxSize, colorBoxY + legendBoxSize, paint)
            paint.color = resources.getColor(android.R.color.black)
            canvas.drawText(labels[i], colorBoxX + legendBoxSize + legendSpacing + textOffset, colorBoxY + legendBoxSize, paint)
        }
    }


    private fun getDatesFromLast7Days(): List<String> {
        val dates = ArrayList<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())

        for (i in 0..6) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            dates.add(dateFormat.format(calendar.time))
            calendar.time = Date()
        }

        return dates.reversed()
    }


    fun setUsername(username: String) {
        this.username = username
        // Możesz tutaj zaktualizować wyświetlanie lub logikę wykresu
        invalidate()
    }


}