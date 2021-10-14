package com.ssheetz.weathermap.repository

import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import com.ssheetz.weathermap.repository.openweather.OpenWeatherRetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit


class Repository (private val database: WeatherDatabase, private val retrofit: Retrofit) {

    // Fetch the forecast for a known location by place id from local database
    fun forecast(placeid: Long) : Flow<ForecastData?> {
        return flow {
            val elements = database.forecastDao().getForecasts(placeid)
            val place = database.placesDao().getPlace(placeid)
            emit(ForecastData(place, elements))
        }
    }

    // Fetch the forecast for a place by lat/lon from OpenWeather.com and also store it in database
    fun forecast(lat: Double, lon: Double)  : Flow<ForecastData?> {
        val retrofit = retrofit.create(OpenWeatherRetrofitService::class.java)
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
                            entry.weather[0].main,
                            entry.weather[0].description,
                            entry.weather[0].icon,
                            entry.main.temp,
                            entry.main.feels_like,
                            entry.main.pressure,
                            entry.main.humidity,
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

    // Fetch a list of all places stored in the local database
    suspend fun getSavedLocations() : List<ForecastPlace> {
        return database.placesDao().getPlaces()
    }
}