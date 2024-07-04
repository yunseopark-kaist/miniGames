package com.example.minigames.ui.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minigames.adapter.Score
import org.json.JSONArray
import java.io.IOException

class GalleryViewModel : ViewModel() {

    private val _scores = MutableLiveData<List<Score>>()
    val scores: LiveData<List<Score>> = _scores

    fun loadScoresFromJson(context: Context) {
        val jsonString: String
        try {
            jsonString = context.assets.open("scores.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val scoreList = mutableListOf<Score>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val score = jsonObject.getInt("score")
                scoreList.add(Score(name, score))
            }
            _scores.value = scoreList
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}