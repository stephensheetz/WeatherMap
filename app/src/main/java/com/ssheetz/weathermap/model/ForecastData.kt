package com.ssheetz.weathermap.model

data class ForecastData(
    val cityName: String,
    val forecasts: List<ForecastElement>
)