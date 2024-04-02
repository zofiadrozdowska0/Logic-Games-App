package com.example.maze

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.absoluteValue


class MazeView : View {

    private lateinit var maze: Array<BooleanArray>
    private lateinit var ballView: View
    private var cellWidth = 0f
    private var cellHeight = 0f
    private var ballPositionX = 0
    private var ballPositionY = 0
    private var redRectPositionX = 0
    private var redRectPositionY = 0


    private val wallPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val passagePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val redRectPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setMaze(maze: Array<BooleanArray>, ballView: View) {
        this.maze = maze
        this.ballView = ballView
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!::maze.isInitialized) return

        for (i in maze.indices) {
            for (j in maze[i].indices) {
                val left = j * cellWidth
                val top = i * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight

                if (maze[i][j]) {
                    canvas.drawRect(left, top, right, bottom, wallPaint)
                } else {
                    canvas.drawRect(left, top, right, bottom, passagePaint)
                }
            }
        }

        // Umieść kulkę na pierwszym wolnym polu z jednym sąsiadem
        // Umieść kulkę na pierwszym wolnym polu z jednym sąsiadem lub znajdź najdalsze wolne pole z jednym sąsiadem
        // Umieść kulkę na pierwszym wolnym polu z jednym sąsiadem lub znajdź najdalsze wolne pole z jednym sąsiadem
        // Umieść kulkę na pierwszym wolnym polu z jednym sąsiadem lub znajdź najdalsze wolne pole z jednym sąsiadem
        // Umieść kulkę na pierwszym wolnym polu z jednym sąsiadem lub znajdź najdalsze wolne pole z jednym sąsiadem
        if (ballPositionX == 0 && ballPositionY == 0) {
            var singleNeighborX = 0
            var singleNeighborY = 0
            var singleNeighborFound = false

            var farthestX = 0
            var farthestY = 0
            var maxDistance = -1

            for (i in 1 until maze.size - 1) {
                for (j in 1 until maze[0].size - 1) {
                    if (!maze[i][j] && countSurroundingSpaces(i, j) == 1) {
                        if (!singleNeighborFound) {
                            singleNeighborX = i
                            singleNeighborY = j
                            singleNeighborFound = true
                        }

                        val distance = (i - singleNeighborX).absoluteValue + (j - singleNeighborY).absoluteValue
                        if (distance > maxDistance) {
                            maxDistance = distance
                            farthestX = i
                            farthestY = j
                        }
                    }
                }
            }

            // Ustaw pozycję kuli na pierwszym wolnym polu z jednym sąsiadem
            if (singleNeighborFound) {
                ballPositionX = singleNeighborX
                ballPositionY = singleNeighborY

                // Ustaw pozycję kuli
                val ballCenterX = (ballPositionY * cellWidth) + (cellWidth / 2) - (ballView.width / 2)
                val ballCenterY = (ballPositionX * cellHeight) + (cellHeight / 2) - (ballView.height / 2)
                ballView.x = ballCenterX
                ballView.y = ballCenterY
            }

            // Znajdź najdalsze wolne pole z jednym sąsiadem od kuli
            for (i in 1 until maze.size - 1) {
                for (j in 1 until maze[0].size - 1) {
                    if (!maze[i][j] && countSurroundingSpaces(i, j) == 1) {
                        val distance = (i - ballPositionX).absoluteValue + (j - ballPositionY).absoluteValue
                        if (distance > maxDistance) {
                            maxDistance = distance
                            farthestX = i
                            farthestY = j
                        }
                    }
                }
            }

            // Ustaw czerwony prostokąt na najdalszym wolnym polu od kuli
            redRectPositionX = farthestX
            redRectPositionY = farthestY
        }

// Narysuj czerwony prostokąt na planszy
        if (redRectPositionX != 0 && redRectPositionY != 0) {
            val left = redRectPositionY * cellWidth
            val top = redRectPositionX * cellHeight
            val right = left + cellWidth
            val bottom = top + cellHeight
            canvas.drawRect(left, top, right, bottom, redRectPaint)
        }






    }


    fun isBallOnRedRectangle(ballX: Float, ballY: Float): Boolean {
        // Oblicz pozycję pola na podstawie położenia kuli
        val col = (ballX / cellWidth).toInt()
        val row = (ballY / cellHeight).toInt()

        // Sprawdź, czy pole jest czerwonym prostokątem
        return col == redRectPositionY && row == redRectPositionX
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellWidth = w.toFloat() / maze.size.toFloat()
        cellHeight = h.toFloat() / maze[0].size.toFloat()

        // Ustaw pozycję czerwonego prostokąta po zmianie rozmiaru
        redRectPositionX = ((h / cellHeight / 3) * 2).toInt()
        redRectPositionY = ((w / cellWidth / 3) * 2).toInt()
    }


    private fun countSurroundingSpaces(x: Int, y: Int): Int {
        var count = 0
        if (!maze[x - 1][y]) count++ // lewo
        if (!maze[x + 1][y]) count++ // prawo
        if (!maze[x][y - 1]) count++ // góra
        if (!maze[x][y + 1]) count++ // dół
        return count
    }
}