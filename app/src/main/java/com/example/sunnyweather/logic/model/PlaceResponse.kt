package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

// 定义一个数据类，用于表示地点响应信息，包含状态和地点列表
data class PlaceResponse(val status: String,
                         @SerializedName("pois") val places: List<Place>)

// 定义一个数据类，用于表示单个地点信息，包括名称、位置和格式化地址
data class Place(
    val name: String, val pname: String, val cityname: String,
    val address: String, val aname: String, val location: String,val adcode: String){
    val addressInfo: String
        get() = "$pname $cityname $address"
    val locationInfo: Location
        get() = Location(lng = location.split(",")[0], lat = location.split(",")[1])
}

// 定义一个数据类，用于表示位置信息，包括经度和纬度
data class Location(val lng: String, val lat: String)
