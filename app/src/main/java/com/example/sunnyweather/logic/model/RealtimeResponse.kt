package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

data class RealtimeResponse(val status: String, val lives: List<LiveWeather>)
data class LiveWeather(val weather: String,val temperature_float: Float,val humidity: String)