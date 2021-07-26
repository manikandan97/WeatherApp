package com.weatherapp.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherapp.app.databinding.LayoutWeatherItemBinding
import com.weatherapp.app.model.HourlyItem
import com.weatherapp.app.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var weatherList = mutableListOf<HourlyItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContent(weatherList[position])
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    class ViewHolder(private val binding: LayoutWeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setContent(weather: HourlyItem) {
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("IST")
            binding.txtTimeStamp.text = formatter.format(Date(weather.dt * 1000))
            binding.txtDescription.text = weather.weather[0].main
            binding.txtHumidity.text = "Humidity : "  + weather.humidity
            binding.txtWindSpeed.text = "Wind Speed : "  + weather.windSpeed
            binding.txtTemp.text = round(weather.temp / 10).toString() + "°C"
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutWeatherItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setData(newData: WeatherResponse){
        for (i in 0..11){
            weatherList.add(newData.hourly[i])
        }
        notifyDataSetChanged()
    }
}