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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository): ViewModel() {

    private val resultsLiveData: MutableLiveData<ForecastData> = MutableLiveData()
    fun getResults() : LiveData<ForecastData> = resultsLiveData

    private val loadingStateLiveData: MutableLiveData<LoadingState> = MutableLiveData()
    fun getLoadingState() : LiveData<LoadingState> = loadingStateLiveData

    private val mapStateLiveData: MutableLiveData<MapState> = MutableLiveData()
    fun getMapState() : LiveData<MapState> = mapStateLiveData

    private val  savedLocationsLiveData: MutableLiveData<SavedLocations> = MutableLiveData()

    init {
        // Default map position: center over the U.S.
        mapStateLiveData.value = MapState(40.0, -96.0, 2.0, false)
    }

    fun getSavedLocations() : LiveData<SavedLocations> {
        // Perform an initial sync with database for saved locations
        CoroutineScope(Dispatchers.IO).launch {
            savedLocationsLiveData.postValue(SavedLocations(
                repository.getSavedLocations(),
                -1))
        }
        return savedLocationsLiveData
    }

    // Get forecast by known place ID
    // Move map and zoom to level 8.0
    fun selectSavedLocation(location: Long, savedLocationPos: Int) {
        viewModelScope.launch {
            repository.forecast(location).collect {
                resultsLiveData.value = it.data
                loadingStateLiveData.value = if (it.data?.forecasts?.isNotEmpty() == true) LoadingState.DONE else LoadingState.EMPTY
                mapStateLiveData.value = if (it.data?.place != null) MapState(it.data.place.latitude, it.data.place.longitude, 8.0, true) else null
                val places = savedLocationsLiveData.value?.places
                savedLocationsLiveData.value = SavedLocations(places ?: emptyList(), savedLocationPos)
            }
        }
    }

    // Get forecast by new lat/lng
    fun tapNewLocation(lat: Double, lon: Double, zoom: Double) {
        loadingStateLiveData.value = LoadingState.LOADING
        mapStateLiveData.value = MapState(lat, lon, Math.max(zoom, 8.0), true)
        viewModelScope.launch {
            repository.forecast(lat, lon).collect {
                resultsLiveData.value = it.data
                loadingStateLiveData.value = if (it.data?.forecasts?.isNotEmpty() == true) LoadingState.DONE else LoadingState.EMPTY
                val locations = repository.getSavedLocations()

                // Find newly created location?
                var currentPos = -1
                for (i in locations.indices) {
                    if (locations[i].id == (it.data?.place?.id ?: -1)) {
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