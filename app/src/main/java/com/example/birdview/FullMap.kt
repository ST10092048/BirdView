package com.example.birdview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.birdview.databinding.ActivityFullMapBinding
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class FullMap : AppCompatActivity() {
    private lateinit var binding: ActivityFullMapBinding
    var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
    }
}