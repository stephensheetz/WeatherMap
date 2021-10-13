package com.ssheetz.weathermap.repository.openweather

data class ForecastEntry (
    val dt: Long,
    val main: ForecastMain,
    val weather: List<ForecastWeather>,
    val wind: ForecastWind
)