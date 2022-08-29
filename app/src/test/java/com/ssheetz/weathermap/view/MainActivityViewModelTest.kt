package com.ssheetz.weathermap.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ssheetz.weathermap.TestCoroutineRule
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.ForecastElement
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.repository.Repository
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.anyDouble
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    lateinit var viewModel: MainActivityViewModel
    lateinit var searchObserver: Observer<ForecastData>
    lateinit var repository: Repository
    lateinit var sampleData: ForecastData

    // Needed to test with LiveData
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    // Needed to test with coroutines
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = mock()
        viewModel = MainActivityViewModel(repository)
        searchObserver = mock()

        sampleData = ForecastData(
            ForecastPlace(123L, "some city", -88.1, 33.5), listOf(
            ForecastElement(0, 123, 1002, "mainname", "some weather conditions", "someicon.png", 44.4f, 33.3f, 950f,77f, 23.1f, 223.0f)
        ))

        Mockito.`when`(repository.forecast(anyDouble(), anyDouble())).thenReturn(flow{ emit(sampleData) })
    }


    @Test
    fun forecastUpdatesLiveDataWithForecastFromRepository() {
        testCoroutineRule.runBlockingTest {

            viewModel.getResultsObserver().observeForever(searchObserver)
            Mockito.`when`(repository.getSavedLocations()).thenReturn(
                listOf( ForecastPlace(114L, "", -23.2, 44.5) )
            )

            viewModel.tapNewLocation(-45.1, 44.1, 7.0)

            val forecasts = viewModel.getResultsObserver().value?.forecasts
            val place = viewModel.getResultsObserver().value?.place
            assertEquals(1, forecasts?.size)
            assertEquals(123L, place?.id)
        }
    }
}

