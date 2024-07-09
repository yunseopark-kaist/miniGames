package com.example.minigames.ui.home

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.ui.sudoku.SudokuActivity
import com.example.minigames.ui.sudoku.SudokuGameState
import com.google.gson.Gson
import java.io.File

class HomeViewModel : ViewModel() {

    private val _gameList = MutableLiveData<List<GameItem>>()
    val gameList: LiveData<List<GameItem>> get() = _gameList

    fun loadGameList(filesDir: File) {
        val games = filesDir.listFiles { file -> file.extension == "json" }?.map { file ->
            val json = file.readText()
            val gson = Gson()
            val gameState = gson.fromJson(json, SudokuGameState::class.java)
            val progress = calculateProgress(gameState)
            GameItem("sudoku", file.nameWithoutExtension, gameState.timeTaken, progress)
        } ?: emptyList()

        _gameList.postValue(games)
    }

    private fun calculateProgress(gameState: SudokuGameState): Int {
        val targetCells = gameState.cells.filter { !it.isStartingCell }
        val filledCells = targetCells.count { it.value != 0 && it.isCorrect }
        return (filledCells * 100) / targetCells.size
    }

    fun deleteGame(filesDir: File, gameName: String) {
        val file = File(filesDir, "$gameName.json")
        if (file.exists()) {
            file.delete()
            loadGameList(filesDir)
        }
    }
}