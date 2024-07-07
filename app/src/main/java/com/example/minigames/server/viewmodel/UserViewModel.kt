package com.example.minigames.server.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigames.server.model.User
import com.example.minigames.server.network.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    fun createUser(id: Int, nickname: String){
        viewModelScope.launch{
            try{
                RetrofitClient.userService.createUser(User(id, nickname))
                Log.d("create", "success: $id: $nickname")
            }
            catch(e: Exception){
                Log.d("create", "failed$id: $nickname")
            }
        }
    }
    fun getUsers(){
        viewModelScope.launch{
            try{
                val users = RetrofitClient.userService.getUsers()
                Log.d("get: ", "success ${users[0].nickname}")
            } catch(e: Exception){
                Log.d("get", "exception");
            }
        }
    }

}