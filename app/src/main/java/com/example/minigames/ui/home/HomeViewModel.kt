package com.example.minigames.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _gameList = MutableLiveData<List<GameItem>>().apply {
        value = listOf(
            // Sample data
            GameItem("Type1", "Game 1", 10, 50),
            GameItem("Type2", "Game 2", 20, 70)
        )
    }
    val gameList: LiveData<List<GameItem>> = _gameList
}