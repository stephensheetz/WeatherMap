package com.ssheetz.weathermap.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_title)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Show instructions first time after launch
        if (savedInstanceState == null) {
            AlertDialog.Builder(this)
                .setTitle(R.string.main_title)
                .setMessage(R.string.instructions)
                .create()
                .show()
        }
    }

}