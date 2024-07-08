package com.example.minigames.ui.sudoku

class SudokuGenerator {
    private val size = 9
    private val sqrtSize = 3
    private val solution = Array(size) { IntArray(size) }

    fun generateSolution(): Array<IntArray> {
        fillBoard()
        return solution
    }

    private fun fillBoard(): Boolean {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (solution[row][col] == 0) {
                    val numbers = (1..size).shuffled()
                    for (num in numbers) {
                        if (isValid(row, col, num)) {
                            solution[row][col] = num
                            if (fillBoard()) return true
                            solution[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until size) {
            if (solution[row][i] == num || solution[i][col] == num) return false
        }
        val boxRow = row / sqrtSize * sqrtSize
        val boxCol = col / sqrtSize * sqrtSize
        for (i in boxRow until boxRow + sqrtSize) {
            for (j in boxCol until boxCol + sqrtSize) {
                if (solution[i][j] == num) return false
            }
        }
        return true
    }
}