package com.ssheetz.weathermap.model

data class SavedLocations (
    val places: List<ForecastPlace>,
    val currentPos: Int
)