package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 定义服务创建器单例对象
object ServiceCreator {
    // 定义基础URL常量
    private const val BASE_URL = "https://restapi.amap.com/"
    // 构建Retrofit实例，配置基础URL和Gson转换器工厂
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    // 创建指定类型的服务实例
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    // 内联函数，创建指定具体类型的服务实例
    inline fun <reified T> create(): T = create(T::class.java)
}
