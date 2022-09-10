package com.ssheetz.weathermap.model

data class ForecastDataResult(
    val data: ForecastData?,
    val error: Exception?
)