package com.ssheetz.weathermap.view

import android.content.Context
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
import com.ssheetz.weathermap.model.MapState
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel

class MapFragment : Fragment() {

    private var viewModel: MainActivityViewModel? = null
    private val locationsAdapter = LocationsAdapter()
    private lateinit var mapView: MapView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Mapbox.getInstance(context, getString(R.string.mapbox_access_token))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            viewModel = ViewModelProvider(it).get(MainActivityViewModel::class.java)
        }

        viewModel?.let {vm ->
            val spinner = view.findViewById<Spinner>(R.id.spinner_locations)
            spinner.adapter = locationsAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    //val place = locationsAdapter.getItem(position) as ForecastPlace
                    //showProgressBar()
                    vm.selectSavedLocation(id, position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            vm.getSavedLocationsObserver().observe(viewLifecycleOwner, {
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
        mapView.getMapAsync {mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded.

                mapboxMap.addOnMapClickListener { point ->
                    //showProgressBar()
                    viewModel?.tapNewLocation(
                        point.latitude,
                        point.longitude,
                        mapboxMap.cameraPosition.zoom
                    )
                    true
                }

                viewModel?.getMapStateObserver()?.observe(viewLifecycleOwner, { mapState: MapState ->
                    mapboxMap.clear()
                    if (mapState.hasMarker) {
                        mapboxMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    mapState.latitude,
                                    mapState.longitude
                                )
                            )
                        )
                    }

                    // Move camera to selected location?
                    val camPosition = CameraPosition.Builder()
                        .target(LatLng(mapState.latitude, mapState.longitude))
                        .zoom(mapState.zoom)
                        .build()
                    mapboxMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(camPosition), 2000
                    )
                })
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }
}