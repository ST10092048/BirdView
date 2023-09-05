package com.example.birdview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivityFullMapBinding
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class FullMap : AppCompatActivity() {
    private lateinit var binding: ActivityFullMapBinding
    lateinit var toggle : ActionBarDrawerToggle
    var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        //This code is for the side bar
        toggle = ActionBarDrawerToggle(this@FullMap, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navViews.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.Map -> {
                    val map = Intent(this, FullMap::class.java)
                    startActivity(map)
                }
                R.id.Entry -> {
                    val entry = Intent(this, BirdEntry::class.java)
                    startActivity(entry)
                }
                R.id.Category ->{
                    val category = Intent(this, SpecieCatgeory::class.java)
                    startActivity(category)
                }
                R.id.logout -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
            }
            true
            // The code ends here
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}