package com.example.sunnyweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place

// 定义一个适配器类，用于将Place数据列表适配到RecyclerView中
class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    // 内部类ViewHolder，用于保存RecyclerView中每个项的视图组件
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName) // 获取并保存地点名称的TextView
        val placeAddress: TextView = view.findViewById(R.id.placeAddress) // 获取并保存地点地址的TextView
    }

    // 创建ViewHolder实例，用于加载每个列表项的视图
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.place_item, // 指定列表项的布局文件
            parent, false)
        return ViewHolder(view) // 返回ViewHolder实例
    }

    // 绑定ViewHolder到具体的数据项，设置视图内容
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position] // 获取当前位置的地点数据
        holder.placeName.text = place.name // 设置地点名称
        holder.placeAddress.text = place.addressInfo // 设置地点地址
    }

    // 返回列表项的总数
    override fun getItemCount() = placeList.size
}
