package com.example.minigames.server.viewmodel

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.minigames.server.network.RetrofitClient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.server.model.SaveGameDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GameViewModel : ViewModel() {

    private val _loadGameResult = MutableLiveData<List<SaveGameDto>>()
    val loadGameResult: LiveData<List<SaveGameDto>> = _loadGameResult

    private val _saveGameResult = MutableLiveData<Boolean>()
    val saveGameResult: LiveData<Boolean> get() = _saveGameResult

    fun saveGame(saveGameDto: SaveGameDto) {
        Log.d("saveGame", "Function start: Saving game ${saveGameDto.name}")
        RetrofitClient.gameService.saveGame(saveGameDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("saveGame", "Saved successfully: ${saveGameDto.name}")
                    _saveGameResult.value = true
                } else {
                    Log.d("saveGame", "Save failed for ${saveGameDto.name}")
                    _saveGameResult.value = false
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("saveGame", "Save failed with error: ${t.message} for ${saveGameDto.name}")
                _saveGameResult.value = false
            }
        })
    }

    fun loadGames(userId: String) {
        Log.d("loadGames", "Function start: Loading games for user $userId")
        RetrofitClient.gameService.loadGames(userId).enqueue(object : Callback<List<SaveGameDto>> {
            override fun onResponse(call: Call<List<SaveGameDto>>, response: Response<List<SaveGameDto>>) {
                if (response.isSuccessful) {
                    Log.d("loadGames", "Loaded successfully for user $userId")
                    response.body()?.let { games ->
                        _loadGameResult.value = games
                    }
                } else {
                    Log.d("loadGames", "Load failed for user $userId")
                }
            }

            override fun onFailure(call: Call<List<SaveGameDto>>, t: Throwable) {
                Log.d("loadGames", "Load failed with error: ${t.message} for user $userId")
            }
        })
    }


    fun testServerConnection() {
        val saveGameDto = SaveGameDto("testUserId", "testGameName", "{\"state\":\"test\"}")
        RetrofitClient.gameService.saveGame(saveGameDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("testServerConnection", "Connection successful")
                } else {
                    Log.d("testServerConnection", "Connection failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("testServerConnection", "Connection failed with error: ${t.message}")
            }
        })
    }
}