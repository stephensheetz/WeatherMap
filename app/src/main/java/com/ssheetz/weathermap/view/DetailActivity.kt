package com.ssheetz.weathermap.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.databinding.ActivityDetailBinding
import com.ssheetz.weathermap.viewmodel.DetailActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLACE_ID = "extra_place_id"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
    }

    private lateinit var viewModel: DetailActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setTitle(R.string.main_title)
        viewModel = ViewModelProvider(this).get(DetailActivityViewModel::class.java)

        viewModel.getResults().observe(this) { data ->
            val forecast = data.forecasts[0]

            // Load the image icon from URL
            val imageUrl = "https://openweathermap.org/img/wn/${data.forecasts[0].icon}@2x.png"
            Glide.with(viewBinding.imgWeather).load(imageUrl).into(viewBinding.imgWeather)

            // Format forecast time
            val simpleDateFormat = SimpleDateFormat("EEEE hh:mm aaa")
            val dateTime = simpleDateFormat.format(forecast.timeUnixUTC * 1000).toString()
            viewBinding.tvTime.text = dateTime

            viewBinding.tvPlaceName.text = data.place.name
            viewBinding.tvLatitude.text = data.place.latitude.toString()
            viewBinding.tvLongitude.text = data.place.longitude.toString()
            viewBinding.tvDescription.text = forecast.description
            viewBinding.tvTemperature.text = forecast.temp.toString() + " K"
            viewBinding.tvFeelsLike.text = forecast.feels_like.toString() + " K"
            viewBinding.tvPressure.text = forecast.pressure.toString() + " bar"
            viewBinding.tvHumidity.text = forecast.humidity.toString() + " %"
            viewBinding.tvWindSpeed.text = " %.1f m/s".format(forecast.windSpeed)
            viewBinding.tvWindDirection.text = " %.0f deg".format(forecast.windDirection)
        }

        // Request data from view model
        val placeId = intent.getLongExtra(EXTRA_PLACE_ID, -1)
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, -1)
        viewModel.refreshSavedLocation(placeId, timestamp)
    }
}