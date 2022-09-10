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

    private val resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()
    fun getResults() : LiveData<ForecastData> = resultsLiveData

    fun refreshSavedLocation(location: Long, timestamp: Long) {
        // Get forecast by known place ID
        viewModelScope.launch {
            repository.forecast(location).collect { result ->
                // find only the requested timestamp
                result.data?.let { data ->
                    data.forecasts.find { elem -> elem.timeUnixUTC == timestamp }?.let {
                        resultsLiveData.value = ForecastData(data.place, listOf(it))
                    }
                }
            }
        }
    }
}