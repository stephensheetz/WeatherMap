package com.ssheetz.weathermap

import com.ssheetz.weathermap.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Provides
    fun provideRepository(): Repository {
        return Repository()
    }
}