package com.ssheetz.weathermap.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="places", indices = [Index(value = ["name"], unique = true)])
data class ForecastPlace (
    @PrimaryKey val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double
)