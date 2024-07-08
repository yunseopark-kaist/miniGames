package com.example.minigames.server.viewmodel

import android.util.Log
import com.example.minigames.server.network.RetrofitClient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.server.model.SaveGameDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameViewModel : ViewModel() {
    private val _games = MutableLiveData<List<SaveGameDto>>()
    val games: LiveData<List<SaveGameDto>> get() = _games

    private val _saveGameResult = MutableLiveData<Boolean>()
    val saveGameResult: LiveData<Boolean> get() = _saveGameResult

    fun saveGame(saveGameDto: SaveGameDto) {
        Log.d("saveGame", "funtion start")
        RetrofitClient.gameService.saveGame(saveGameDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("saveGame", "saved successfully")
                    _saveGameResult.value = true
                } else {
                    Log.d("saveGame", "save failed")
                    _saveGameResult.value = false
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("saveGame", "save failed with error: ${t.message}")
                _saveGameResult.value = false
            }
        })
    }

    fun loadGames(userId: String) {
        RetrofitClient.gameService.loadGames(userId).enqueue(object : Callback<List<SaveGameDto>> {
            override fun onResponse(call: Call<List<SaveGameDto>>, response: Response<List<SaveGameDto>>) {
                if (response.isSuccessful) {
                    _games.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<SaveGameDto>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}