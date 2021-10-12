package com.ssheetz.weathermap.repository.database

import androidx.room.*
import com.ssheetz.weathermap.model.ForecastPlace

@Dao
interface PlacesDao {
    @Query("SELECT * FROM places ORDER BY name ASC")
    suspend fun getPlaces(): List<ForecastPlace>

    @Query("SELECT * FROM places WHERE id LIKE :placeId")
    suspend fun getPlace(placeId: Long): ForecastPlace

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg places: ForecastPlace)

    @Delete
    suspend fun delete(place: ForecastPlace)
}