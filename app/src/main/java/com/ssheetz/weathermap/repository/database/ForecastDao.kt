package com.ssheetz.weathermap.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ssheetz.weathermap.model.ForecastElement

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecasts WHERE placeId LIKE :placeId ORDER BY timeUnixUTC ASC")
    fun getForecasts(placeId: Long): List<ForecastElement>

    @Insert
    fun insertAll(forecasts: List<ForecastElement>)

    @Delete
    fun delete(forecast: ForecastElement)
}