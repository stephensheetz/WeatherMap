package com.ssheetz.weathermap.repository

import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import com.ssheetz.weathermap.repository.openweather.ForecastResult
import com.ssheetz.weathermap.repository.openweather.OpenWeatherRetrofitService
import com.ssheetz.weathermap.repository.openweather.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


class Repository (private val database: WeatherDatabase) {

    fun forecast(placeid: Long, callback: (ForecastData?) -> Unit) {
        runBlocking(Dispatchers.Default) {
            val elements = database.forecastDao().getForecasts(placeid)
            val place = database.placesDao().getPlace(placeid)
            callback(ForecastData(place, elements))
        }
    }

    fun forecast(lat: Double, lon: Double, callback: (ForecastData?) -> Unit) {
        runBlocking(Dispatchers.Default) {
            val retrofit =
                RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
            val call = retrofit.forecast(lat, lon, OpenWeatherRetrofitService.appid)
            enqueueForecastCall(call, lat, lon, callback)
        }
    }

    private fun enqueueForecastCall(call: Call<ForecastResult>, lat: Double, lon: Double, callback: (ForecastData?) -> Unit) {
        call.enqueue(object : retrofit2.Callback<ForecastResult> {
            override fun onResponse(call: Call<ForecastResult>, response: Response<ForecastResult>) {
                if (response.isSuccessful) {
                    val entries = ArrayList<ForecastElement>()
                    val place = ForecastPlace(
                        response.body()?.city?.id ?: 0,
                        response.body()?.city?.name ?: "",
                        lat,
                        lon)
                    for (entry in response.body()?.list ?: emptyList()) {
                        entries.add(
                            ForecastElement(
                        0,
                            place.id,
                            entry.dt,
                            entry.weather[0].description,
                            entry.weather[0].icon,
                            entry.wind.speed,
                            entry.wind.deg
                        )
                        )
                    }
                    runBlocking(Dispatchers.Default) {
                        database.placesDao().insertAll(place)
                        database.forecastDao().insertAll(entries)
                        callback(ForecastData(place, entries))
                    }
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<ForecastResult>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getSavedLocations() : List<ForecastPlace> {
        return database.placesDao().getPlaces()
    }
}