package com.ssheetz.weathermap.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssheetz.weathermap.model.ForecastPlace


class LocationsAdapter : BaseAdapter() {

    private var locations : List<ForecastPlace> = emptyList()

    fun setLocations(locations: List<ForecastPlace>) {
        this.locations = locations
    }

    override fun getCount(): Int {
        return locations.size
    }

    override fun getItem(p0: Int): Any {
        return locations[p0]
    }

    override fun getItemId(p0: Int): Long {
        return locations[p0].id
    }

    override fun getView(p0: Int, p1: View?, parent: ViewGroup?): View {
        val row = LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        row.findViewById<TextView>(android.R.id.text1).text = locations[p0].name
        return row
    }
}