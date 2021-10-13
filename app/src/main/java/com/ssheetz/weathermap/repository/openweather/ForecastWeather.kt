package com.ssheetz.weathermap.repository.openweather

data class ForecastWeather (
    val main: String,
    val description: String,
    val icon: String,
)