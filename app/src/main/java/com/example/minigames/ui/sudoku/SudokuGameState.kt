package com.example.minigames.ui.sudoku

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException

data class SudokuGameState(
    val solution: Array<IntArray>,
    val cells: List<Cell>,
    val timeTaken: Long,
    val incorrectAttempts: Int
){
    private fun getGameStateFile(context: Context): File {
        return File(context.filesDir, "sudoku_game_state.json")
    }

    fun saveGameState(context: Context, gameId: String, solution: Array<IntArray>, cells: List<Cell>, timeTaken: Long, incorrectAttempts: Int) {
        val gameState = SudokuGameState(
            solution = solution,
            cells = cells.map { cell ->
                Cell(
                    row = cell.row,
                    col = cell.col,
                    value = cell.value,
                    isStartingCell = cell.isStartingCell,
                    notes = cell.notes,
                    isCorrect = cell.isCorrect
                )
            },
            timeTaken = timeTaken,
            incorrectAttempts = incorrectAttempts
        )

        val gson = Gson()
        val json = gson.toJson(gameState)
        val file = getGameStateFile(context)
        try {
            FileWriter(file).use { writer ->
                writer.write(json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadGameState(context: Context): SudokuGameState? {
        val file = getGameStateFile(context)
        if (!file.exists()) {
            return null
        }

        val gson = Gson()
        return try {
            file.bufferedReader().use { reader ->
                val json = reader.readText()
                gson.fromJson(json, SudokuGameState::class.java)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}