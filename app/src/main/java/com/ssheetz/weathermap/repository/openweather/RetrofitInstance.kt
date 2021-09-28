package com.ssheetz.weathermap.repository.openweather


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private const val baseUrl = "https://api.openweathermap.org/data/2.5/"

        // set this value for testing with a mock
        var retrofit: Retrofit? = null

        fun getInstance() : Retrofit {
            return retrofit ?: Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}