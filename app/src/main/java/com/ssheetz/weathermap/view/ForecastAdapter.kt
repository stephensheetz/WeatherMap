package com.ssheetz.weathermap.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.model.*
import java.text.SimpleDateFormat


class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ResultsViewHolder>() {

    private var forecastData : ForecastData? = null

    fun setForecastResult(result: ForecastData) {
        forecastData = result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.forecast_row, parent, false)
        return ResultsViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        forecastData?.let {
            holder.bind(it.forecasts.get(position))
        }
    }

    override fun getItemCount(): Int {
        return forecastData?.forecasts?.size ?: 0
    }

    class ResultsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val rowView = view
        private val textViewDayTime: TextView = view.findViewById(R.id.textView_daytime)
        private val textViewDecription: TextView = view.findViewById(R.id.textView_description)
        private val imageViewThumb: ImageView = view.findViewById(R.id.imageView_thumbnail)

        fun bind(data: ForecastElement) {

            // Format forecast time
            val simpleDateFormat = SimpleDateFormat("EEEE\nhh:mm aaa")
            val dateTime = simpleDateFormat.format(data.timeUnixUTC * 1000).toString()
            textViewDayTime.text = dateTime

            // Compose the text to go alongside the image
            val desc = StringBuilder()
                .append(data.description)
                .appendLine()
                .append(rowView.context.getString(R.string.wind_speed))
                .append(" %.1f".format(data.windSpeed))
                .append(" m/s\n")
                .append(rowView.context.getString(R.string.wind_direction))
                .append(" %.0f".format(data.windDirection))
                .append(" degrees")
                .toString()
            textViewDecription.text = desc

            // Load the image icon from URL
            val imageUrl = "https://openweathermap.org/img/wn/${data.icon}@2x.png"
            Glide.with(imageViewThumb).load(imageUrl).into(imageViewThumb)

            // Launch something upon tap?
            rowView.setOnClickListener {v ->

            }
        }
    }
}