// SudokuGame.kt
package com.example.minigames.ui.sudoku

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

data class SudokuGameState(
    val solution: Array<IntArray>,
    val cells: List<Cell>,
    val timeTaken: Long,
    val incorrectAttempts: Int,
)

class SudokuGame {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val isSolvedLiveData = MutableLiveData<Boolean>()
    val isFailedLiveData = MutableLiveData<Boolean>()
    val scoreLiveData = MutableLiveData<Int>()
    val incorrectAttemptsLiveData = MutableLiveData<Int>()
    val timeTakenLiveData = MutableLiveData<Long>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false
    private var incorrectAttempts = 0
    private var score = 1000
    private var startTime = System.currentTimeMillis()
    private var timer: Timer? = null

    private var board: Board
    private var cells: List<Cell>
    private var solution: Array<IntArray>

    init {
        val generator = SudokuGenerator()
        solution = generator.generateSolution()
        cells = (0 until 9).flatMap { row ->
            (0 until 9).map { col ->
                Cell(row, col, solution[row][col], isStartingCell = true)
            }
        }
        board = Board(9, cells)

        makePuzzle()

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        incorrectAttemptsLiveData.postValue(incorrectAttempts)
        scoreLiveData.postValue(score)
        startTimer()
    }

    private fun makePuzzle() {
        val numEmptyCells = 40
        val random = Random(System.currentTimeMillis())
        repeat(numEmptyCells) {
            var row: Int
            var col: Int
            do {
                row = random.nextInt(9)
                col = random.nextInt(9)
            } while (board.getCell(row, col).value == 0)
            board.getCell(row, col).value = 0
            board.getCell(row, col).isStartingCell = false
            board.getCell(row, col).isCorrect = false
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val timeTaken = currentTime - startTime
                timeTakenLiveData.postValue(timeTaken)
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            if (cell.value != number) {
                if (solution[selectedRow][selectedCol] != number) {
                    incorrectAttempts++
                    incorrectAttemptsLiveData.postValue(incorrectAttempts)
                    updateScore()
                    if (incorrectAttempts >= 4) {
                        isFailedLiveData.postValue(true)
                        return
                    }
                }
            }
            cell.value = number
            cell.isCorrect = (solution[selectedRow][selectedCol] == number)
        }
        cellsLiveData.postValue(board.cells)
        checkIfSolved()
    }

    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
            cell.isCorrect = false
        }
        cellsLiveData.postValue(board.cells)
    }

    private fun updateScore() {
        score = when (incorrectAttempts) {
            1 -> 900
            2 -> 700
            3 -> 300
            else -> 0
        }
        scoreLiveData.postValue(score)
    }

    private fun checkIfSolved() {
        val isSolved = board.cells.all { it.isStartingCell || it.isCorrect }
        if (isSolved) {
            stopTimer()
            val endTime = System.currentTimeMillis()
            timeTakenLiveData.postValue(endTime - startTime)
            isSolvedLiveData.postValue(true)
        }
    }

    private fun getGameStateFile(context: Context, gameName: String): File {
        return File(context.filesDir, "$gameName.json")
    }

    fun saveGameState(context: Context, gameName: String) {
        stopTimer()
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
            timeTaken = timeTakenLiveData.value ?: 0,
            incorrectAttempts = incorrectAttempts,
        )

        val gson = Gson()
        val json = gson.toJson(gameState)
        val file = getGameStateFile(context, gameName)
        try {
            FileWriter(file).use { writer ->
                writer.write(json)
                Log.d("saveGameState", "saved game state")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadGameState(context: Context, gameName: String): SudokuGameState? {
        val file = getGameStateFile(context, gameName)
        if (!file.exists()) return null
        return try {
            val json = file.readText()
            val gson = Gson()
            gson.fromJson(json, SudokuGameState::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun restoreGameState(gameState: SudokuGameState) {
        solution = gameState.solution
        cells = gameState.cells
        incorrectAttempts = gameState.incorrectAttempts
        startTime = System.currentTimeMillis() - gameState.timeTaken

        board = Board(9, cells)
        cellsLiveData.postValue(board.cells)
        incorrectAttemptsLiveData.postValue(incorrectAttempts)
        scoreLiveData.postValue(score)

        startTimer()
    }
}
