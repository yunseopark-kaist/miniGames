package com.example.minigames.server.viewmodel.userViewModel

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


    fun isThereId(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val exists = RetrofitClient.userService.isThereId(id)
                callback(exists)
                if (exists) {
                    Log.d("isThereId", "User with id $id exists")
                } else {
                    Log.d("isThereId", "User with id $id does not exist")
                }
            } catch (e: Exception) {
                Log.e("isThereId", "failed to check id $id", e)
                // 예외 처리
                callback(false) // 예외 발생 시에도 false를 콜백으로 전달
            }
        }
    }

    fun updateUser(id: Int, nickname: String) {
        viewModelScope.launch {
            try {
                val updatedUser = RetrofitClient.userService.updateUser(id, nickname)
                Log.d("updateUser", "success: $updatedUser")
            } catch (e: Exception) {
                Log.e("updateUser", "failed to update user with id $id", e)
            }
        }
    }

    fun removeUser(id: Int) {
        viewModelScope.launch {
            try {
                val removedUser = RetrofitClient.userService.removeUser(id)
                Log.d("removeUser", "success: $removedUser")
            } catch (e: Exception) {
                Log.e("removeUser", "failed to remove user with id $id", e)
            }
        }
    }

}