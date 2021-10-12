package com.ssheetz.weathermap.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssheetz.weathermap.R
import com.ssheetz.weathermap.databinding.FragmentDetailBinding
import com.ssheetz.weathermap.viewmodel.MainActivityViewModel

class DetailFragment : Fragment() {

    private var viewModel: MainActivityViewModel? = null
    private val forecastAdapter = ForecastAdapter()
    private var viewBinding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            viewModel = ViewModelProvider(it).get(MainActivityViewModel::class.java)
        }

        viewModel?.let {
            it.getResultsObserver().observe(viewLifecycleOwner, {
                if (it != null && it.forecasts.isNotEmpty()) {
                    forecastAdapter.setForecastResult(it)
                    forecastAdapter.notifyDataSetChanged()
                    showResults()
                } else {
                    showNoResults()
                    Toast.makeText(requireActivity(), R.string.api_error, Toast.LENGTH_LONG).show()
                }
            })
        }

        view.findViewById<RecyclerView>(R.id.recyclerView_results).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forecastAdapter
        }
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun showResults() {
        viewBinding?.recyclerViewResults?.visibility = View.VISIBLE
        viewBinding?.textViewNoResults?.visibility = View.INVISIBLE
        viewBinding?.progressBar?.visibility = View.GONE
    }

    private fun showNoResults() {
        viewBinding?.recyclerViewResults?.visibility = View.INVISIBLE
        viewBinding?.textViewNoResults?.visibility = View.VISIBLE
        viewBinding?.progressBar?.visibility = View.GONE
    }

    private fun showProgressBar() {
        viewBinding?.recyclerViewResults?.visibility = View.INVISIBLE
        viewBinding?.textViewNoResults?.visibility = View.INVISIBLE
        viewBinding?.progressBar?.visibility = View.VISIBLE
    }

}