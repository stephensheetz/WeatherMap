package com.ssheetz.weathermap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailActivityViewModel @Inject constructor(
    private val repository: Repository): ViewModel() {
    private var resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()

    fun getResultsObserver() : LiveData<ForecastData> {
        return resultsLiveData
    }

    // Get forecast by known place ID
    fun fetchSavedLocation(location: Long, timestamp: Long) {
        viewModelScope.launch {
            repository.forecast(location).collect { flowed ->
                // find only the requested timestamp
                flowed.data?.let { data ->
                    data.forecasts.find { elem -> elem.timeUnixUTC == timestamp }?.let {
                        resultsLiveData.value = ForecastData(data.place, listOf(it))
                    }
                }
            }
        }
    }
}