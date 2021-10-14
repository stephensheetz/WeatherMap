package com.ssheetz.weathermap.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.databinding.ActivityDetailBinding
import com.ssheetz.weathermap.viewmodel.DetailActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLACE_ID = "extra_place_id"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
    }

    private lateinit var viewModel: DetailActivityViewModel
    private var viewBinding: ActivityDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding!!.root)
        setTitle(R.string.main_title)
        viewModel = ViewModelProvider(this).get(DetailActivityViewModel::class.java)

        viewModel.getResultsObserver().observe(this, {data ->
            Log.d("MYTAG", data.forecasts.get(0).windSpeed.toString())
        })

        // Request data from view model
        val placeId = intent.getLongExtra(EXTRA_PLACE_ID, -1)
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, -1)
        viewModel.fetchSavedLocation(placeId, timestamp)
    }
}