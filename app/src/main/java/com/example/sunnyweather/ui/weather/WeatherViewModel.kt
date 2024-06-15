package com.example.sunnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Location
/*
在WeatherViewModel中定义了locationLng、locationLat和placeName这3
个变量，它们都是和界面相关的数据，放到ViewModel中可以保证它们在手机屏幕发生旋转的时
候不会丢失
 */
class WeatherViewModel : ViewModel() {
    private val adcodeLiveData = MutableLiveData<String>()
    var adcode = ""
    var placeName = ""
    val weatherLiveData = adcodeLiveData.switchMap{ adcode ->
        println("WeatherViewModel 检测到adcodeLiveData变化，开始刷新天气，执行Repository.refreshWeather()")
        Repository.refreshWeather(adcode)
    }
    fun refreshWeather(adcode: String) {
        adcodeLiveData.value = adcode
    }
}
