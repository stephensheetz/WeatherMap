package com.ssheetz.weathermap.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.SavedLocations
import com.ssheetz.weathermap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository): ViewModel() {
    private var resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()
    private var savedLocationsLiveData: MutableLiveData<SavedLocations> = MutableLiveData()
    var changedSelection: Boolean = false

    fun getResultsObserver() : LiveData<ForecastData> {
        return resultsLiveData
    }

    fun getSavedLocationsObserver() : LiveData<SavedLocations> {
        // Perform an initial sync with database for saved locations
        runBlocking(Dispatchers.Default) {
            savedLocationsLiveData.postValue(SavedLocations(
                repository.getSavedLocations(),
                -1))
        }
        return savedLocationsLiveData
    }

    // Get forecast by known place ID
    fun forecast(location: Long) {
        repository.forecast(location) {
            resultsLiveData.postValue(it)
        }
    }

    // Get forecast by new lat/lng
    fun forecast(lat: Double, lon: Double) {
        repository.forecast(lat, lon) {
            resultsLiveData.postValue(it)
            val locations = repository.getSavedLocations()

            // Find newly created location?
            var currentPos = -1
            for (i in 0..locations.size-1) {
                if (locations[i].id == it?.place?.id ?: -1) {
                    currentPos = i
                    changedSelection = true
                }
            }

            savedLocationsLiveData.postValue(
                SavedLocations(
                locations,
                currentPos
            )
            )
        }
    }
}