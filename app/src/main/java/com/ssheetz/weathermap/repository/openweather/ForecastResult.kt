package com.ssheetz.weathermap.repository.openweather

data class ForecastResult (
    val list: List<ForecastEntry>,
    val city: ForecastCity
)