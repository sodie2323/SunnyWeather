package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

/*
新建一个Repository单例类，作为仓库层的统一封装入口
 */
object Repository {
    //它使用liveData构建器来创建一个LiveData对象，该对象在后台线程（Dispatchers.IO）中执行异步操作。
    // 这个函数名为searchPlaces，它接受一个名为query的字符串参数，该参数用于搜索地点。
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            println("查询返回的原始数据为：${placeResponse}")
            if (placeResponse.status == "1") {
                println("查询成功")
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is"+
                    "${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        println("Repository.searchPlaces() result is $result")
        emit(result)
    }
}