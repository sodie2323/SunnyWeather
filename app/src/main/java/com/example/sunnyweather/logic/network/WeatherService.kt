package com.sunnyweather.android.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.RealtimeResponse
import com.sunnyweather.android.logic.model.DailyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    // weather/weatherInfo?city=110101&key=e7159ca7516735ee25491cbad21e0e34

    @GET("v3/weather/weatherInfo?key=${SunnyWeatherApplication.KEY}")
    fun getRealtimeWeather(@Query("city") adcode: String): Call<RealtimeResponse>

    @GET("v3/weather/weatherInfo?key=${SunnyWeatherApplication.KEY}&extensions=all")
    fun getDailyWeather(@Query("city") adcode: String): Call<DailyResponse>

}