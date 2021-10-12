package com.ssheetz.weathermap.repository.openweather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherRetrofitService {

    @GET("forecast")
    suspend fun forecast(@Query("q") locationQuery : String,
                 @Query("appid") appid : String) : Response<ForecastResult>

    @GET("forecast")
    suspend fun forecast(@Query("lat") latitude : Double,
                 @Query("lon") longitude : Double,
                 @Query("appid") appid : String) : Response<ForecastResult>

    companion object {
        //const val appid = "f29c7c77f7f5de2caa22ad9c92e2fe54"
        const val appid = "c6e381d8c7ff98f0fee43775817cf6ad"

    }
}
