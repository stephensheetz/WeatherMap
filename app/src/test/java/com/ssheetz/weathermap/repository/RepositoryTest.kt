package com.ssheetz.weathermap.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.database.ForecastDao
import com.ssheetz.weathermap.repository.database.PlacesDao
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import com.ssheetz.weathermap.repository.openweather.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import retrofit2.Response
import retrofit2.Retrofit

class RepositoryTest {

    private lateinit var retrofit: Retrofit
    private lateinit var openweather: OpenWeatherRetrofitService
    private lateinit var searchResponse: Response<ForecastResult>
    private lateinit var repository: Repository
    private lateinit var database: WeatherDatabase
    private lateinit var forecastDao: ForecastDao
    private lateinit var placesDao: PlacesDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        database = mock()
        retrofit = mock()
        placesDao = mock()
        forecastDao = mock()
        repository = Repository(database, retrofit)
        openweather = mock()

        searchResponse = Response.success(
            ForecastResult(listOf(
            ForecastEntry(1200,
                ForecastMain(75.1f, 74.0f, 1000.4f, 45.6f),
                listOf(ForecastWeather("main name", "some conditions", "someicon.png")),
                ForecastWind(12.2f, 220.0f)
            )
        ), ForecastCity(123, "some city", -75.0, 35.5)
            )
        )

        Mockito.`when`(database.forecastDao()).thenReturn(forecastDao)
        Mockito.`when`(database.placesDao()).thenReturn(placesDao)
    }


    @Test
    fun forecastReturnsFlowWithForecasts() = runBlocking {
        val expectedCity = ForecastPlace(123, "some city", -75.0, 35.5)
        Mockito.`when`(openweather.forecast(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())).thenReturn(searchResponse)
        Mockito.`when`(retrofit.create(OpenWeatherRetrofitService::class.java)).thenReturn(openweather)

        val firstPlace = repository.forecast(-75.0, 35.5).first().data?.place

        assertEquals(expectedCity, firstPlace)
    }
}