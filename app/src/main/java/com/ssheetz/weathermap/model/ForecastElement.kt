package com.ssheetz.weathermap.model

data class ForecastElement (
    val timeUnixUTC: Long,
    val description: String,
    val icon: String,
    val windSpeed: Float,
    val windDirection: Float
)