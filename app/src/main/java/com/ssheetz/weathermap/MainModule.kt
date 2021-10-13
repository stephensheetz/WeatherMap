package com.ssheetz.weathermap

import android.content.Context
import androidx.room.Room
import com.ssheetz.weathermap.repository.Repository
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val baseUrl = "https://api.openweathermap.org/data/2.5/"

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideRepository(@ApplicationContext appContext: Context): Repository {
        val database = Room
            .databaseBuilder(appContext, WeatherDatabase::class.java, "weather")
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return Repository(database, retrofit)
    }
}