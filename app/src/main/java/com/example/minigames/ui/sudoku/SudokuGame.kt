// SudokuGame.kt
package com.example.minigames.ui.sudoku

import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

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

    private val board: Board
    private val cells: List<Cell>
    private val solution: Array<IntArray>

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
            val endTime = System.currentTimeMillis()
            timeTakenLiveData.postValue(endTime - startTime)
            isSolvedLiveData.postValue(true)
        }
    }
}
