package com.example.maze


class MazeGrid(private val maze: Array<IntArray>, private val redRectanglePosition: Pair<Int, Int>) {

    fun getMaze(): Array<IntArray> {
        return maze
    }

    fun getRedRectanglePosition(): Pair<Int, Int> {
        return redRectanglePosition
    }
}

