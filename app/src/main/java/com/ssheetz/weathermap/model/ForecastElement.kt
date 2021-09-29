package com.ssheetz.weathermap.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="forecasts")
data class ForecastElement (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val placeId: Long,
    val timeUnixUTC: Long,
    val description: String,
    val icon: String,
    val windSpeed: Float,
    val windDirection: Float
)