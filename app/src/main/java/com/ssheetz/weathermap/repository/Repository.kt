package com.ssheetz.weathermap.repository

import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import com.ssheetz.weathermap.repository.openweather.OpenWeatherRetrofitService
import com.ssheetz.weathermap.repository.openweather.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class Repository (private val database: WeatherDatabase) {

    fun forecast(placeid: Long) : Flow<ForecastData?> {
        return flow {
            val elements = database.forecastDao().getForecasts(placeid)
            val place = database.placesDao().getPlace(placeid)
            emit(ForecastData(place, elements))
        }
    }

    fun forecast(lat: Double, lon: Double)  : Flow<ForecastData?> {
        val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
        return flow {
            // exectute API call and map to model object
            try {
                val response = retrofit.forecast(lat, lon, OpenWeatherRetrofitService.appid)
                val entries = ArrayList<ForecastElement>()
                val place = ForecastPlace(
                    response.body()?.city?.id ?: 0,
                    response.body()?.city?.name ?: "",
                    lat,
                    lon
                )
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
                database.placesDao().insertAll(place)
                database.forecastDao().insertAll(entries)
                emit(ForecastData(place, entries))
            }
            catch(e: Exception) {   // Problem getting the forecast
                emit(null)
            }
        }.flowOn(Dispatchers.IO) // Use the IO thread for this Flow
    }


    /*
        return flow {
            val retrofit = RetrofitInstance.getInstance().create(OpenWeatherRetrofitService::class.java)
            val call = retrofit.forecast(lat, lon, OpenWeatherRetrofitService.appid)
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
                        emit(ForecastData(place, entries))
                    }
                } else {
                    emit(null)
                }
            }
            override fun onFailure(call: Call<ForecastResult>, t: Throwable) {
                callback(null)
            }
        })
    }
*/

    suspend fun getSavedLocations() : List<ForecastPlace> {
        return database.placesDao().getPlaces()
    }
}