package com.ssheetz.weathermap.repository.openweather

data class ForecastCity (
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double
)