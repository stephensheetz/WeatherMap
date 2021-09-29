package com.ssheetz.weathermap.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.database.ForecastDao
import com.ssheetz.weathermap.repository.database.PlacesDao
import com.ssheetz.weathermap.repository.database.WeatherDatabase
import com.ssheetz.weathermap.repository.openweather.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RepositoryTest {

    lateinit var retrofit: Retrofit
    lateinit var openweather: OpenWeatherRetrofitService
    lateinit var call: Call<ForecastResult>
    lateinit var searchResponse: Response<ForecastResult>
    lateinit var repository: Repository
    lateinit var database: WeatherDatabase
    lateinit var forecastDao: ForecastDao
    lateinit var placesDao: PlacesDao

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<Callback<ForecastResult>>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        database = mock()
        repository = Repository(database)
        retrofit = mock()
        RetrofitInstance.retrofit = retrofit
        placesDao = mock()
        forecastDao = mock()

        searchResponse = Response.success(
            ForecastResult(listOf(
            ForecastEntry(1200,
                listOf(ForecastWeather("some conditions", "someicon.png")),
                ForecastWind(12.2f, 220.0f)
            )
        ), ForecastCity(123, "some city", -75.0, 35.5)
            )
        )

        Mockito.`when`(database.forecastDao()).thenReturn(forecastDao)
        Mockito.`when`(database.placesDao()).thenReturn(placesDao)

        call = mock<Call<ForecastResult>> {
            on { execute() } doReturn searchResponse
        }

        Mockito.`when`(call.enqueue(callbackCaptor.capture()))
            .then { callbackCaptor.value.onResponse(call, searchResponse) }

        openweather = mock {
            on { forecast(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()) } doReturn call
        }

        Mockito.`when`(retrofit.create(OpenWeatherRetrofitService::class.java)).thenReturn(openweather)
    }


    @Test
    fun forecastCallsCallbackWithForecasts() {
        val callbackMock: (ForecastData?) -> Unit = mock()
        val expectedCity = ForecastPlace(123, "some city", -75.0, 35.5)
        repository.forecast(-75.0, 35.5, callbackMock)
        argumentCaptor<ForecastData>().apply {
            verify(callbackMock, times(1)).invoke(capture())
            assertEquals(expectedCity, firstValue.place)
        }
    }
}