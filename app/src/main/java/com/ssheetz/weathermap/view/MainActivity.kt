package com.ssheetz.weathermap.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.model.ForecastPlace
import dagger.hilt.android.AndroidEntryPoint
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_title)
        forecastAdapter = ForecastAdapter()
        locationsAdapter = LocationsAdapter()

        initViewModel()

        initViews()

        mapView = findViewById(R.id.mapbox_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            val mapboxMap = it
            it.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded.
                //Now you can add data or make other map adjustmentsit.
                mapboxMap.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(40.0, -96.0))
                    .zoom(2.0)
                    .build()

                mapboxMap.addOnMapClickListener { point ->
                    showProgressBar()
                    mapboxMap.clear()
                    mapboxMap.addMarker(MarkerOptions().position(point))
                    viewModel.forecast(point.latitude, point.longitude)
                    true
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getResultsObserver().observe(this, {
            if (it != null && it.forecasts.isNotEmpty()) {
                forecastAdapter.setForecastResult(it)
                forecastAdapter.notifyDataSetChanged()
                showResults()
            } else {
                showNoResults()
                Toast.makeText(this, R.string.api_error, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.getSavedLocationsObserver().observe(this, {
            if (it != null) {
                locationsAdapter.setLocations(it.places)
                locationsAdapter.notifyDataSetChanged()
                if (it.currentPos > -1) {
                    val spinner = findViewById<Spinner>(R.id.spinner_locations)
                    spinner.setSelection(it.currentPos)
                }
            }
        })
    }

    private fun initViews() {
        findViewById<RecyclerView>(R.id.recyclerView_results).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = forecastAdapter
        }

        val spinner = findViewById<Spinner>(R.id.spinner_locations)
        spinner.adapter = locationsAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val place = locationsAdapter.getItem(position) as ForecastPlace
                showProgressBar()
                mapView.getMapAsync {
                    it.clear()
                    it.addMarker(MarkerOptions().position(LatLng(place.latitude, place.longitude)))

                    // Move camera to selected location?
                    if (!viewModel.changedSelection) {
                        val camPosition = CameraPosition.Builder()
                            .target(LatLng(place.latitude, place.longitude))
                            .zoom(8.0)
                            .build()
                        it.animateCamera(
                            CameraUpdateFactory.newCameraPosition(camPosition), 2000
                        )
                    }
                    viewModel.changedSelection = false;
                }
                viewModel.forecast(id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun showResults() {
        findViewById<View>(R.id.recyclerView_results).visibility = View.VISIBLE
        findViewById<View>(R.id.textView_no_results).visibility = View.INVISIBLE
        findViewById<View>(R.id.progressBar).visibility = View.GONE
    }
    
    private fun showNoResults() {
        findViewById<View>(R.id.recyclerView_results).visibility = View.INVISIBLE
        findViewById<View>(R.id.textView_no_results).visibility = View.VISIBLE
        findViewById<View>(R.id.progressBar).visibility = View.GONE
    }

    private fun showProgressBar() {
        findViewById<View>(R.id.recyclerView_results).visibility = View.INVISIBLE
        findViewById<View>(R.id.textView_no_results).visibility = View.INVISIBLE
        findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}