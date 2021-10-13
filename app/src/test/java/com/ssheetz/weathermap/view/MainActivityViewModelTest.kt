package com.ssheetz.weathermap.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.Repository
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock


class MainActivityViewModelTest {

    lateinit var viewModel: MainActivityViewModel
    lateinit var searchObserver: Observer<ForecastData>
    lateinit var repository: Repository
    lateinit var sampleData: ForecastData

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = mock()
        viewModel = MainActivityViewModel(repository)
        searchObserver = mock()

        sampleData = ForecastData(
            ForecastPlace(123, "some city", -88.1, 33.5), listOf(
            ForecastElement(0, 123, 1002, "some weather conditions", "someicon.png", 23.1f, 223.0f)
        ))

        val captor = argumentCaptor<(ForecastData?) -> Unit>()
        Mockito.`when`(repository.forecast(anyDouble(), anyDouble(), captor.capture()))
            .then { captor.firstValue.invoke(sampleData) }
    }


    @Test
    fun forecastReceivesCallbackWithForecasts() {
        viewModel.getResultsObserver().observeForever(searchObserver)
        viewModel.forecast(-45.1, 44.1, 7.0)

        val arg = ArgumentCaptor.forClass(ForecastData::class.java)
        verify(searchObserver).onChanged(arg.capture())

        val forecasts = arg.allValues[0].forecasts
        val place = arg.allValues[0].place
        assertEquals(1, forecasts.size)
        assertEquals(123, place.id)
    }
}

