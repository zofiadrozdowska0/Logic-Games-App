package com.example.maze

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
    private var treasurePositionX = 0
    private var treasurePositionY = 0
    private lateinit var treasureBitmap: Bitmap
    private lateinit var backgroundBitmap: Bitmap

    private val passagePaint = Paint().apply {
        color = Color.WHITE // Kolor ścieżek
        style = Paint.Style.FILL
    }

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        // Załaduj obrazek skarbu
        treasureBitmap = BitmapFactory.decodeResource(resources, R.drawable.treasure)
        // Załaduj obrazek tła
        backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background_texture)
    }

    fun setMaze(maze: Array<BooleanArray>, ballView: View) {
        this.maze = maze
        this.ballView = ballView
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!::maze.isInitialized) return

        // Narysuj tło
        canvas.drawBitmap(backgroundBitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)

        for (i in maze.indices) {
            for (j in maze[i].indices) {
                val left = j * cellWidth
                val top = i * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight

                if (!maze[i][j]) {
                    canvas.drawRect(left, top, right, bottom, passagePaint)
                }
            }
        }

        if (ballPositionX == 0 && ballPositionY == 0) {
            initializeBallAndTreasurePositions()
        }

        if (treasurePositionX != 0 && treasurePositionY != 0) {
            val left = treasurePositionY * cellWidth
            val top = treasurePositionX * cellHeight
            val right = left + cellWidth
            val bottom = top + cellHeight
            canvas.drawBitmap(treasureBitmap, null, RectF(left, top, right, bottom), null)
        }
    }

    private fun initializeBallAndTreasurePositions() {
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

        if (singleNeighborFound) {
            ballPositionX = singleNeighborX
            ballPositionY = singleNeighborY

            val ballCenterX = (ballPositionY * cellWidth) + (cellWidth / 2) - (ballView.width / 2)
            val ballCenterY = (ballPositionX * cellHeight) + (cellHeight / 2) - (ballView.height / 2)
            ballView.x = ballCenterX
            ballView.y = ballCenterY
        }

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

        treasurePositionX = farthestX
        treasurePositionY = farthestY
    }

    fun isBallOnTreasure(ballX: Float, ballY: Float): Boolean {
        val col = (ballX / cellWidth).toInt()
        val row = (ballY / cellHeight).toInt()
        return col == treasurePositionY && row == treasurePositionX
    }

    fun moveBall(dx: Int, dy: Int) {
        val newPosX = ballPositionX + dx
        val newPosY = ballPositionY + dy

        if (canMoveTo(newPosX, newPosY)) {
            ballPositionX = newPosX
            ballPositionY = newPosY

            val ballCenterX = (ballPositionY * cellWidth) + (cellWidth / 2) - (ballView.width / 2)
            val ballCenterY = (ballPositionX * cellHeight) + (cellHeight / 2) - (ballView.height / 2)
            ballView.x = ballCenterX
            ballView.y = ballCenterY
            invalidate()
        }
    }

    private fun canMoveTo(x: Int, y: Int): Boolean {
        return x in 0 until maze.size && y in 0 until maze[0].size && !maze[x][y]
    }

    fun checkCollision(ballX: Float, ballY: Float): Boolean {
        val col = (ballX / cellWidth).toInt()
        val row = (ballY / cellHeight).toInt()
        return col in 0 until maze[0].size && row in 0 until maze.size && !maze[row][col]
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellWidth = w.toFloat() / maze[0].size
        cellHeight = h.toFloat() / maze.size

        treasurePositionX = ((h / cellHeight / 3) * 2).toInt()
        treasurePositionY = ((w / cellWidth / 3) * 2).toInt()
    }

    private fun countSurroundingSpaces(x: Int, y: Int): Int {
        var count = 0
        if (!maze[x - 1][y]) count++
        if (!maze[x + 1][y]) count++
        if (!maze[x][y - 1]) count++
        if (!maze[x][y + 1]) count++
        return count
    }
}