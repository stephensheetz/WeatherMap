package com.ssheetz.weathermap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssheetz.weathermap.model.ForecastData
import com.ssheetz.weathermap.model.LoadingState
import com.ssheetz.weathermap.model.MapState
import com.ssheetz.weathermap.model.SavedLocations
import com.ssheetz.weathermap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository): ViewModel() {
    private var resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()
    private var loadingStateLiveData: MutableLiveData<LoadingState> = MutableLiveData()
    private var savedLocationsLiveData: MutableLiveData<SavedLocations> = MutableLiveData()
    private var mapStateLiveData: MutableLiveData<MapState> = MutableLiveData()

    init {
        // Default map position: center over the U.S.
        mapStateLiveData.value = MapState(40.0, -96.0, 2.0, false)
    }

    fun getResultsObserver() : LiveData<ForecastData> {
        return resultsLiveData
    }

    fun getLoadingStateObserver() : LiveData<LoadingState> {
        return loadingStateLiveData
    }

    fun getSavedLocationsObserver() : LiveData<SavedLocations> {
        // Perform an initial sync with database for saved locations
        CoroutineScope(Dispatchers.IO).launch {
            savedLocationsLiveData.postValue(SavedLocations(
                repository.getSavedLocations(),
                -1))
        }
        return savedLocationsLiveData
    }

    fun getMapStateObserver() : LiveData<MapState> {
        return mapStateLiveData
    }

    // Get forecast by known place ID
    // Move map and zoom to level 8.0
    fun selectSavedLocation(location: Long, savedLocationPos: Int) {
        viewModelScope.launch {
            repository.forecast(location).collect {
                resultsLiveData.value = it
                loadingStateLiveData.value = if (it?.forecasts?.isNotEmpty() ?: false) LoadingState.DONE else LoadingState.EMPTY
                mapStateLiveData.value = if (it?.place != null) MapState(it.place.latitude, it.place.longitude, 8.0, true) else null
                val places = savedLocationsLiveData.value?.places
                savedLocationsLiveData.value = SavedLocations(places ?: emptyList(), savedLocationPos)
            }
        }
    }

    // Get forecast by new lat/lng
    fun forecast(lat: Double, lon: Double, zoom: Double) {
        loadingStateLiveData.value = LoadingState.LOADING
        mapStateLiveData.value = MapState(lat, lon, Math.max(zoom, 8.0), true)
        viewModelScope.launch {
            repository.forecast(lat, lon).collect {
                resultsLiveData.value = it
                loadingStateLiveData.value = if (it?.forecasts?.isNotEmpty() ?: false) LoadingState.DONE else LoadingState.EMPTY
                val locations = repository.getSavedLocations()

                // Find newly created location?
                var currentPos = -1
                for (i in 0..locations.size - 1) {
                    if (locations[i].id == it?.place?.id ?: -1) {
                        currentPos = i
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
}