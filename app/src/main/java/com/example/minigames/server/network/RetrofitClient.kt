package com.example.minigames.server.network
import com.example.minigames.server.model.SaveGameDto
import com.example.minigames.server.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import java.io.File

interface UserService {
    @POST("users")
    suspend fun createUser(@Body user: User)

    @GET("users")
    suspend fun getUsers(@Query("id") id: Int? = null): List<User>

    @GET("users/toprankings")
    fun getTopRankings(): Call<List<User>>

    @GET("users/exists")
    suspend fun isThereId(@Query("id") id: Int?=null): Boolean

    @PUT("users/scoreup")
    suspend fun userScoreUp(@Query("id") id: Int, @Body delta: Map<String,Int>):User

    @PUT("users/nickname")
    suspend fun updateUser(@Query("id") id: Int, @Body nickname:Map<String, String>): User

    @DELETE("users")
    suspend fun removeUser(@Query("id") id: Int?=null): User

    @Multipart
    @POST("users/uploadProfileImage")
    suspend fun uploadProfileImage(@Query("id")id: Int, @Part file: MultipartBody.Part): Boolean
}

interface GameService {
    @POST("games")
    fun saveGame(@Body saveGameDto: SaveGameDto): Call<Void>

    @GET("games")
    fun loadGames(@Query("userId") userId: String): Call<List<SaveGameDto>>
}

object RetrofitClient{
    private const val BASE_URL = "http://143.248.177.153:3000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
        .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃 설정
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val gameService: GameService = retrofit.create(GameService::class.java)
}

object ImageUploader {
    suspend fun uploadImage(userId: Int, imageFile: File): Boolean {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        return try {
            RetrofitClient.userService.uploadProfileImage(userId, body)
        } catch (e: Exception) {
            false
        }
    }
}