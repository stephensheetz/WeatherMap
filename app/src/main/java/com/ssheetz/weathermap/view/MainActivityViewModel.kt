package com.ssheetz.weathermap.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository): ViewModel() {

    var resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()
    var savedLocationsLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun getResultsObserver() : LiveData<ForecastData> {
        return resultsLiveData
    }

    fun getSavedLocationsObserver() : LiveData<List<String>> {
        return savedLocationsLiveData
    }

    fun forecast(location: String) {
        repository.forecast(location) {
            resultsLiveData.postValue(it)
        }
    }

    fun forecast(lat: Double, lon: Double) {
        repository.forecast(lat, lon) {
            resultsLiveData.postValue(it)
        }
    }
}