package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    /**
     * 保存地点信息到SharedPreferences
     * @param place 要保存的地点对象
     */
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    /**
     * 从SharedPreferences获取保存的地点信息
     * @return 地点对象
     */
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    /**
     * 检查地点信息是否已保存
     * @return 是否已保存
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")

    /**
     * 获取SharedPreferences实例
     * @return SharedPreferences实例
     */
    private fun sharedPreferences() = SunnyWeatherApplication.context.
    getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}
