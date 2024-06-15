package com.example.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.service.autofill.Validators.or
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.Repository.refreshWeather
import com.example.sunnyweather.logic.model.getSky
import com.sunnyweather.android.logic.model.DailyResponse
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
    val swipeRefresh by lazy { findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeRefresh) }
    override fun onCreate(savedInstanceState: Bundle?) {
        println("WeatherActivity onCreate,天气页面创建")
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        println("WeatherActivity onCreate,设置ContentView")
        setContentView(R.layout.activity_weather)
        println("WeatherActivity onCreate,设置viewModel")
        if (viewModel.adcode.isEmpty()) {
            println("WeatherActivity onCreate,adcode为空，进行赋值")
            viewModel.adcode = intent.getStringExtra("adcode") ?: ""
            println("WeatherActivity onCreate,adcode赋值成功，值为${viewModel.adcode}")
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            println("WeatherActivity onCreate,weatherLiveData被观察到发生变化，result=${result}")
            val weather = result.getOrNull()
            if (weather != null) {
                println("WeatherActivity onCreate,weatherLiveData被观察到，weather不为空，进行解析与展示")
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        val navBtn = findViewById<Button>(R.id.navBtn)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }
    fun refreshWeather() {
        viewModel.refreshWeather(adcode = viewModel.adcode)
        swipeRefresh.isRefreshing = true
    }

    //    于showWeatherInfo()方法中的逻辑就比较简单了，其实就是从Weather对象中获取数
//    据，然后显示到相应的控件上。
    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        println("天气信息:\n realtime=${realtime}\n,daily=${daily}")
        // 填充now.xml布局中的数据
        println("showWeatherInfo 开始填充now.xml布局中的数据")
        val currentTempText = "${realtime.lives.get(0).temperature_float.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.lives.get(0).weather).info
        val currentPM25Text = "空气指数 ${Random().nextInt(20)+10}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.lives.get(0).weather).bg)
        // 填充forecast.xml布局中的数据
        println("showWeatherInfo 开始填充forecast.xml布局中的数据")
        forecastLayout.removeAllViews()
        val days = daily.forecasts[0].casts.size
        val casts = daily.forecasts[0].casts
        println("showWeatherInfo 开始填充${days}天的预报数据")
        for (i in 0 until days) {
            println("showWeatherInfo 开始填充第${i+1}天的预报数据")
            println("skycon")
            val skycon = casts[i].dayweather
            println("temperature")
            val daytemp = casts[i].daytemp
            val nighttemp = casts[i].nighttemp
            println("showWeatherInfo 开始创建forecast_item.xml布局")
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                forecastLayout, false)
            println("获取组件：dateInfo,skyIcon,skyInfo,temperatureInfo")
            val dateInfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo: TextView = view.findViewById(R.id.temperatureInfo)
            println("设置日期")
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(casts[i].date)
            println("设置天气,skycon: $skycon")
            val sky = getSky(skycon)
            println("设置图标")
            skyIcon.setImageResource(sky.icon)
            println("设置文字")
            skyInfo.text = sky.info
            println("设置温度")
            val tempText = "${nighttemp.toInt()} ~ ${daytemp.toInt()} ℃"
            temperatureInfo.text = tempText
            println("showWeatherInfo 开始添加预报数据到forecastLayout")
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        println("showWeatherInfo 开始填充life_index.xml布局中的数据")
        var lifeIndex = daily.lifeIndex
        if (lifeIndex == null) {
            val coldRiskText = DailyResponse.LifeDescription("暂无感冒指数数据")
            val dressingText = DailyResponse.LifeDescription("暂无穿衣指数数据")
            val ultravioletText = DailyResponse.LifeDescription("暂无紫外线指数数据")
            val carWashingText = DailyResponse.LifeDescription("暂无洗车指数数据")
            lifeIndex = DailyResponse.LifeIndex(listOf(coldRiskText), listOf(dressingText), listOf(ultravioletText), listOf(carWashingText) )

        }
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}