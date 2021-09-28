package com.ssheetz.weathermap.repository

import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.repository.openweather.ForecastResult
import com.ssheetz.weathermap.repository.openweather.OpenWeatherRetrofitService
import com.ssheetz.weathermap.repository.openweather.RetrofitInstance
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor() {

    fun forecast(location: String, callback: (ForecastData?) -> Unit) {
        val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
        val call = retrofit.forecast(location, OpenWeatherRetrofitService.appid)
        enqueForecastCall(call, callback)
    }

    fun forecast(lat: Double, lon: Double, callback: (ForecastData?) -> Unit) {
        val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
        val call = retrofit.forecast(lat, lon, OpenWeatherRetrofitService.appid)
        enqueForecastCall(call, callback)
    }

    fun enqueForecastCall(call: Call<ForecastResult>, callback: (ForecastData?) -> Unit) {
        call.enqueue(object : retrofit2.Callback<ForecastResult> {
            override fun onResponse(call: Call<ForecastResult>, response: Response<ForecastResult>) {
                if (response.isSuccessful) {
                    val entries = ArrayList<ForecastElement>()
                    for (entry in response.body()?.list ?: emptyList()) {
                        entries.add(
                            ForecastElement(
                            entry.dt,
                            entry.weather[0].description,
                            entry.weather[0].icon,
                            entry.wind.speed,
                            entry.wind.deg
                        )
                        )
                    }
                    callback(ForecastData(response.body()?.city?.name ?: "", entries))
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<ForecastResult>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getSavedLocations(callback: (List<String>) -> Unit) {

    }
}