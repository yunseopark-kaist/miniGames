import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.server.model.SharedGameDto
import com.example.minigames.server.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SharedGameViewModel : ViewModel() {
    val sharedGamesLiveData = MutableLiveData<List<SharedGameDto>?>()
    val errorLiveData = MutableLiveData<String?>()

    private val sharedGameService = RetrofitClient.sharedGameService

    fun shareGame(fromUserId: String, toUserId: String, name: String, gameState: String) {
        val sharedGameDto = SharedGameDto(fromUserId, toUserId, name, gameState)

        sharedGameService.shareGame(sharedGameDto).enqueue(object : Callback<SharedGameDto> {
            override fun onResponse(call: Call<SharedGameDto>, response: Response<SharedGameDto>) {
                if (response.isSuccessful) {
                } else {
                    errorLiveData.postValue("Error: ${response.message()}")
                    Log.d("shareGame", "error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SharedGameDto>, t: Throwable) {
                errorLiveData.postValue("Failure: ${t.message}")
                Log.d("shareGame", "error: ${t.message}")
            }
        })
    }

    fun getSharedGames(userId: String) {
        sharedGameService.getSharedGames(userId).enqueue(object : Callback<List<SharedGameDto>> {
            override fun onResponse(call: Call<List<SharedGameDto>>, response: Response<List<SharedGameDto>>) {
                if (response.isSuccessful) {
                    sharedGamesLiveData.postValue(response.body())
                } else {
                    errorLiveData.postValue("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<SharedGameDto>>, t: Throwable) {
                errorLiveData.postValue("Failure: ${t.message}")
            }
        })
    }
}