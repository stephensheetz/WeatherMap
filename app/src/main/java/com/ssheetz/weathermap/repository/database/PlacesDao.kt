package com.ssheetz.weathermap.repository.database

import androidx.room.*
import com.ssheetz.weathermap.model.ForecastPlace

@Dao
interface PlacesDao {
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getPlaces(): List<ForecastPlace>

    @Query("SELECT * FROM places WHERE id LIKE :placeId")
    fun getPlace(placeId: Long): ForecastPlace

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg places: ForecastPlace)

    @Delete
    fun delete(place: ForecastPlace)
}