package com.ssheetz.weathermap.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssheetz.weathermap.R

class MainActivity : AppCompatActivity() {
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle(R.string.main_title)
        forecastAdapter = ForecastAdapter()

        initViewModel()

        initViews()

        // do an initial search?
        //viewModel.forecast("chicago")
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getResultsObserver().observe(this, {
            var count = 0
            if (it != null) {
                forecastAdapter.setForecastResult(it)
                forecastAdapter.notifyDataSetChanged()
                count = it.forecasts.size
            } else {
                Toast.makeText(this, R.string.api_error, Toast.LENGTH_LONG).show()
            }

            if (count > 0) {
                showResults()
            } else {
                showNoResults()
            }
        })
    }

    private fun initViews() {
        findViewById<RecyclerView>(R.id.recyclerView_results).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = forecastAdapter
        }

        val editQuery = findViewById<EditText>(R.id.editText_location)
        editQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                forecast()
                true
            } else {
                false
            }
        }
    }

    private fun forecast() {
        val editText = findViewById<EditText>(R.id.editText_location)
        val imm: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        showProgressBar()
        val location = editText.text.toString()
        viewModel.forecast(location)
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
}