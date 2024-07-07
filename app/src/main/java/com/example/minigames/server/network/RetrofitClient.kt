package com.example.minigames.server.network

import com.example.minigames.server.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("users")
    suspend fun createUser(@Body user: User)

    @GET("users")
    suspend fun getAllUsers(): List<User>
    abstract fun getUsers(): List<User>

}

object RetrofitClient{
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
}