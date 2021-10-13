package com.ssheetz.weathermap.model

data class MapState (
    val latitude: Double,
    val longitude: Double,
    val zoom: Double,
    val hasMarker: Boolean
)