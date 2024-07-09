package com.example.minigames.ui.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.server.model.User
import com.example.minigames.server.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingViewModel : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun loadUsersFromServer() {
        val call: Call<List<User>> = RetrofitClient.userService.getTopRankings()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    _users.value = response.body()
                } else {
                    // Handle unsuccessful response (e.g., error parsing JSON, server error)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Handle network failures (e.g., no internet connection)
            }
        })
    }
}