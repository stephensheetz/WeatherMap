package com.ssheetz.weathermap.model

data class ForecastData(
    val place: ForecastPlace,
    val forecasts: List<ForecastElement>
)