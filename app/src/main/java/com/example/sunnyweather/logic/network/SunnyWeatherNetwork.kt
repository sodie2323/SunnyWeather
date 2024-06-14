package com.example.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/*
接下来我们还需要再定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装。
 */
object SunnyWeatherNetwork {
    // 创建PlaceService实例
    private val placeService = ServiceCreator.create<PlaceService>()

    // 异步搜索地点
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    fun testSearchPlaces(query: String) = placeService.searchPlaces(query)
    // 扩展函数，用于异步等待Call结果
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                // 处理成功响应
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null"))
                }
                // 处理失败响应
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}

