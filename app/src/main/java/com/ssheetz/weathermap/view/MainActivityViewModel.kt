package com.ssheetz.weathermap.view

import com.ssheetz.weathermap.repository.openweather.ForecastResult

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssheetz.weathermap.model.*
import com.ssheetz.weathermap.repository.openweather.OpenWeatherRetrofitService
import com.ssheetz.weathermap.repository.openweather.RetrofitInstance
import retrofit2.Call
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    var resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()

    fun getResultsObserver() : MutableLiveData<ForecastData> {
        return resultsLiveData
    }

    fun forecast(location: String) {
        val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
        val call = retrofit.forecast(location, OpenWeatherRetrofitService.appid)
        enqueCall(call)
    }

    fun forecast(lat: Double, lon: Double) {
        val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
        val call = retrofit.forecast(lat, lon, OpenWeatherRetrofitService.appid)
        enqueCall(call)
    }

    fun enqueCall(call: Call<ForecastResult>) {
        call.enqueue(object : retrofit2.Callback<ForecastResult> {
            override fun onResponse(call: Call<ForecastResult>, response: Response<ForecastResult>) {
                if (response.isSuccessful) {
                    val entries = ArrayList<ForecastElement>()
                    for (entry in response.body()?.list ?: emptyList()) {
                        entries.add(ForecastElement(
                            entry.dt,
                            entry.weather[0].description,
                            entry.weather[0].icon,
                            entry.wind.speed,
                            entry.wind.deg
                        ))
                    }
                    resultsLiveData.postValue(ForecastData(response.body()?.city?.name ?: "", entries))
                } else {
                    resultsLiveData.postValue(null)
                }
            }

            override fun onFailure(call: Call<ForecastResult>, t: Throwable) {
                resultsLiveData.postValue(null)
            }
        })

        //savedLocations.add(query)
        //savedLocationsLiveData.postValue(savedLocations)
    }
}