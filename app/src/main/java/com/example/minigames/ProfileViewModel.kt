package com.example.minigames

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel

data class User(val id: String, val nickname: String)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

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

    var profileImageUrl: String?
        get() = prefs.getString("profile_image_url", null)
        set(value) = prefs.edit().putString("profile_image_url", value).apply()


    fun saveLoginInfo(token: String, id: Long?, name: String?, profileImageUrl: String?) {
        accessToken = token
        kakaoId = id
        nickname = name
        this.profileImageUrl = profileImageUrl

    }

    fun clearLoginInfo() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return accessToken != null
    }
}