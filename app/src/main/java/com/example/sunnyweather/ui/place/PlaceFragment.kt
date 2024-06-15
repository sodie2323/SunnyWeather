package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.PlaceResponse
import com.example.sunnyweather.logic.network.PlaceService
import com.example.sunnyweather.logic.network.ServiceCreator
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import com.example.sunnyweather.ui.weather.WeatherActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceFragment : Fragment() {
    // 使用lazy委托初始化ViewModel和UI组件
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private val recyclerView by lazy { requireView().findViewById<RecyclerView>(R.id.recyclerView) }
    private val searchPlaceEdit by lazy { requireView().findViewById<EditText>(R.id.searchPlaceEdit) }
    private val bgImageView by lazy { requireView().findViewById<ImageView>(R.id.bgImageView) }
    private val placeTestButton by lazy { requireView().findViewById<Button>(R.id.placeTestButton) }
    private lateinit var adapter: PlaceAdapter

    // 创建并返回fragment的视图
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    // 在Activity创建后初始化RecyclerView和数据监听
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        在PlaceFragment中进行了判断，如果当前已有存储的城市数据，那么就获取已存储的数据
//        并解析成Place对象，然后使用它的地址码和城市名直接跳转并传递给WeatherActivity
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("adcode", place.adcode)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                println("查询内容：$content")
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        placeTestButton.setOnClickListener {
            val content = "北京"
            val call = SunnyWeatherNetwork.testSearchPlaces(content);
            println("测试网络请求：${call.request()}")
            call.enqueue(object : Callback<PlaceResponse> {
                override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                    if (response.isSuccessful) {
                        val placeResponse = response.body()
                        // 处理 placeResponse
                        println("测试Response：$placeResponse")
                    }
                }

                override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                    // 处理错误
                }
            })
        }
        // 观察ViewModel中的地点数据变化
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            println("places的长度：${places?.size}")
            println("places的内容：$places")
            println("places[0]的内容：${places?.get(0)?.addressInfo}")
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

}

