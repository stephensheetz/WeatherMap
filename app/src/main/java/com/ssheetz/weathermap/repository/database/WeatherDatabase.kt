package com.ssheetz.weathermap.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace

@Database(entities = arrayOf(ForecastElement::class, ForecastPlace::class), version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
    abstract fun placesDao(): PlacesDao
}