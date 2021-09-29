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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideRepository(@ApplicationContext appContext: Context): Repository {
        val database = Room
            .databaseBuilder(appContext, WeatherDatabase::class.java, "weather")
            .build()
        return Repository(database)
    }
}