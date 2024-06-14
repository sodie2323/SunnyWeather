package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

class DailyResponse(val status: String, val forecasts: List<Forecast>,val lifeIndex: LifeIndex) {

    data class Forecast(val casts: List<Cast>)
    data class Cast(val date: Date,val daytemp: Float,val nighttemp: Float,val dayWeather: String)
    class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)

    class LifeDescription(val desc: String)

}

