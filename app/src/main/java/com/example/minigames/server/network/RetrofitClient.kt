package com.example.minigames.server.network

import com.example.minigames.server.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.PUT
import retrofit2.http.DELETE

interface UserService {
    @POST("users")
    suspend fun createUser(@Body user: User)

    @GET("users")
    suspend fun getUsers(@Query("id") id: Int? = null): List<User>

    @GET("users/exists")
    suspend fun isThereId(@Query("id") id: Int?=null): Boolean

    @PUT("users")
    suspend fun updateUser(@Query("id") id: Int?=null, @Body nickname: String): User

    @DELETE("users")
    suspend fun removeUser(@Query("id") id: Int?=null): User
}

object RetrofitClient{
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://143.248.177.153:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
}