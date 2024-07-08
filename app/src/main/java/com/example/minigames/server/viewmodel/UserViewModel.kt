package com.example.minigames.server.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigames.server.model.User
import com.example.minigames.server.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
                Log.d("get", "exception")
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
    fun uploadProfileImage(id: Int, uriString: String?) {
        //val file = uriToFile(uriString)
        val file= createMeaninglessFile()
        viewModelScope.launch {
            try {
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val success = RetrofitClient.userService.uploadProfileImage(id, body)
                Log.d("uploadProfileImage", "success: $success")
            } catch (e: Exception) {
                Log.e("uploadProfileImage", "failed to upload image for user with id $id", e)
                }
            }

    }
    fun uriToFile(uriString: String?){
        try{
            val file = File("demo1.txt")
            file.createNewFile()
        }
        catch(e: Exception){
            Log.d("uriToFile","failed")
        }
    }
    fun createMeaninglessFile(): File {
        // 임시 파일을 생성하여 반환합니다.
        val meaninglessContent = "This file contains meaningless content."
        val file = File.createTempFile("meaningless_file", ".txt")
        file.writeText(meaninglessContent)
        return file
    }
}