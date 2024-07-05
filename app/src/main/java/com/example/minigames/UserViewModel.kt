package com.example.minigames

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel

data class User(val id: String, val nickname: String)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) = prefs.edit().putString("access_token", value).apply()

    var kakaoId: Long?
        get() = prefs.getLong("kakao_id", -1).takeIf { it != -1L }
        set(value) {
            if (value != null) {
                prefs.edit().putLong("kakao_id", value).apply()
            }
        }

    var nickname: String?
        get() = prefs.getString("nickname", null)
        set(value) = prefs.edit().putString("nickname", value).apply()

    fun saveLoginInfo(token: String, kakaoId: Long?, nickname: String?) {
        accessToken = token
        this.kakaoId = kakaoId
        this.nickname = nickname
    }

    fun clearLoginInfo() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return accessToken != null
    }
}