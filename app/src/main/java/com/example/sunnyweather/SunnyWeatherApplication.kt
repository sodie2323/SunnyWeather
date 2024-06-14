package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
    companion object {
        const val KEY = "e7159ca7516735ee25491cbad21e0e34" // 填入你申请到的令牌值

        // 抑制静态字段泄漏警告，声明一个延迟初始化的Context变量
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    // 重写onCreate方法，在应用创建时初始化context
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}