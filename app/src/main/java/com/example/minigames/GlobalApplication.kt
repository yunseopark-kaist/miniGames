package com.example.minigames

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, "068fc944c7ac3ec54e1bdba39e4ecf3c")
    }
}