package com.ssheetz.weathermap.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="forecasts")
data class ForecastElement (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val placeId: Long,
    val timeUnixUTC: Long,
    val mainName: String,
    val description: String,
    val icon: String,
    val temp: Float,
    val feels_like: Float,
    val pressure: Float,
    val humidity: Float,
    val windSpeed: Float,
    val windDirection: Float
)