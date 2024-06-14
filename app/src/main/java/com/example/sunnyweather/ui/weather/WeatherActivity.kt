package com.example.sunnyweather.ui.weather

import android.graphics.Color
import android.os.Bundle
import android.service.autofill.Validators.or
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.getSky
import com.sunnyweather.android.logic.model.Weather
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random


//在onCreate()方法中，首先从Intent中取出经 纬度坐标和地区名称，
//并赋值到WeatherViewModel的相应变量中；然后对weatherLiveData 对象进行观察，
//当获取到服务器返回的天气数据时，就调用showWeatherInfo()方法进行解析与展示；
//最后，调用了WeatherViewModel的refreshWeather()方法来执行一次刷新天气的请求。
class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    val placeName by lazy { findViewById<TextView>(R.id.placeName) }
    val currentTemp by lazy { findViewById<TextView>(R.id.currentTemp) }
    val currentSky by lazy { findViewById<TextView>(R.id.currentSky) }
    val currentAQI by lazy { findViewById<TextView>(R.id.currentAQI) }
    val nowLayout by lazy { findViewById<RelativeLayout>(R.id.nowLayout) }
    val forecastLayout by lazy { findViewById<LinearLayout>(R.id.forecastLayout) }
    val coldRiskText by lazy { findViewById<TextView>(R.id.coldRiskText) }
    val dressingText by lazy { findViewById<TextView>(R.id.dressingText) }
    val ultravioletText by lazy { findViewById<TextView>(R.id.ultravioletText) }
    val carWashingText by lazy { findViewById<TextView>(R.id.carWashingText) }
    val weatherLayout by lazy { findViewById<ScrollView>(R.id.weatherLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        setContentView(R.layout.activity_weather)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }
//    于showWeatherInfo()方法中的逻辑就比较简单了，其实就是从Weather对象中获取数
//    据，然后显示到相应的控件上。
    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.lives.get(0).temperature_float.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.lives.get(0).weather).info
        val currentPM25Text = "空气指数 ${Random().nextInt(20)+10}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.lives.get(0).weather).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.forecasts[0].casts.size
        val casts = daily.forecasts[0].casts
        for (i in 0 until days) {
            val skycon = casts[i].dayWeather
            val daytemp = casts[i].daytemp
            val nighttemp = casts[i].nighttemp
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                forecastLayout, false)
            val dateInfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo: TextView = view.findViewById(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(casts[i].date)
            val sky = getSky(skycon)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${nighttemp.toInt()} ~ ${daytemp.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}