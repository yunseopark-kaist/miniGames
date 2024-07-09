package com.example.minigames.ui.sudoku

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SudokuViewModel(application: Application) : AndroidViewModel(application) {
    val sudokuGame = SudokuGame()
}