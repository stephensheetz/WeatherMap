package com.ssheetz.weathermap.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.model.ForecastPlace
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel

class PrimaryFragment : Fragment() {

    private var viewModel: MainActivityViewModel? = null
    private val locationsAdapter = LocationsAdapter()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_primary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            viewModel = ViewModelProvider(it).get(MainActivityViewModel::class.java)
            Mapbox.getInstance(it, getString(R.string.mapbox_access_token))
        }

        viewModel?.let {
            val spinner = view.findViewById<Spinner>(R.id.spinner_locations)
            spinner.adapter = locationsAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val place = locationsAdapter.getItem(position) as ForecastPlace
                    //showProgressBar()
                    mapView.getMapAsync {
                        it.clear()
                        it.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    place.latitude,
                                    place.longitude
                                )
                            )
                        )

                        // Move camera to selected location?
                        if (viewModel?.changedSelection == false) {
                            val camPosition = CameraPosition.Builder()
                                .target(LatLng(place.latitude, place.longitude))
                                .zoom(8.0)
                                .build()
                            it.animateCamera(
                                CameraUpdateFactory.newCameraPosition(camPosition), 2000
                            )
                        }
                        viewModel?.changedSelection = false
                    }
                    viewModel?.forecast(id)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            it.getSavedLocationsObserver().observe(viewLifecycleOwner, {
                if (it != null) {
                    locationsAdapter.setLocations(it.places)
                    locationsAdapter.notifyDataSetChanged()
                    if (it.currentPos > -1) {
                        val spinner = getView()?.findViewById<Spinner>(R.id.spinner_locations)
                        spinner?.setSelection(it.currentPos)
                    }
                }
            })
        }

        mapView = view.findViewById(R.id.mapbox_view)
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
                    //showProgressBar()
                    mapboxMap.clear()
                    mapboxMap.addMarker(MarkerOptions().position(point))
                    viewModel?.forecast(point.latitude, point.longitude)
                    true
                }
            }
        }
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