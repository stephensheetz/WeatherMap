package com.ssheetz.weathermap.repository.openweather

data class ForecastMain (
    val temp: Float,
    val feels_like: Float,
    val pressure: Float,
    val humidity: Float
)