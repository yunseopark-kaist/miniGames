package com.example.minigames.server.network
import com.example.minigames.server.model.SaveGameDto
import com.example.minigames.server.model.User
import com.example.minigames.ui.sudoku.SudokuGameState
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("users")
    suspend fun createUser(@Body user: User)

    @GET("users")
    suspend fun getUsers(@Query("id") id: Int? = null): List<User>

}

interface GameService {
    @POST("games")
    fun saveGame(@Body saveGameDto: SaveGameDto): Call<Void>

    @GET("games")
    fun loadGames(@Query("userId") userId: String): Call<List<SaveGameDto>>
}

object RetrofitClient{
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://143.248.229.71:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val gameService: GameService = retrofit.create(GameService::class.java)
}