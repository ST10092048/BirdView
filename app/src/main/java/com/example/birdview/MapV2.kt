package com.example.birdview

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.birdview.databinding.ActivityMapV2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface EBirdService {
    @GET("/v2/data/obs/CA/recent")
    fun getBirdSightings(@Query("key") apiKey: String): Call<List<BirdSighting>> //data class

    data class BirdSighting(
        val speciesCode: String,
        val comName: String,
        val sciName: String,
        val locId: String,
        val locName: String,
        val obsDt: String,
        val howMany: Int,
        val lat: Double,
        val lng: Double,
        val obsValid: Boolean,
        val obsReviewed: Boolean,
        val locationPrivate: Boolean,
        val subId: String,
        val exoticCategory: String?
    )
}
    class MapV2 : AppCompatActivity(), OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

        private lateinit var mMap: GoogleMap
        private lateinit var binding: ActivityMapV2Binding
        private lateinit var eBirdService: EBirdService
        private lateinit var fusedLocationClient: FusedLocationProviderClient

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityMapV2Binding.inflate(layoutInflater)
            setContentView(binding.root)

            // Initialize FusedLocationProviderClient
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            // Request location permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            } else {
                initializeMap()
                initializeEBirdService()
                requestLocation()
            }
        }

        private fun initializeMap() {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        private fun initializeEBirdService() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.ebird.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            eBirdService = retrofit.create(EBirdService::class.java)
        }

        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap

            // Enable the My Location layer
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Handle permission request
                return
            }
            mMap.isMyLocationEnabled = true

            // Set click listeners
            mMap.setOnMyLocationButtonClickListener(this)
            mMap.setOnMyLocationClickListener(this)
        }

        private fun requestLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val userLocation = LatLng(it.latitude, it.longitude)
                        // getBirdSightings(userLocation)
                    }
                }
        }

        private fun getBirdSightings() {
            val apiKey = "9riis08rlgc2"

            eBirdService.getBirdSightings(apiKey)
                .enqueue(object : Callback<List<EBirdService.BirdSighting>> {
                    override fun onResponse(
                        call: Call<List<EBirdService.BirdSighting>>,
                        response: Response<List<EBirdService.BirdSighting>>
                    ) {
                        if (response.isSuccessful) {
                            val birdSightings = response.body()

                            if (birdSightings != null) {
                                // Add markers for bird sightings
                                for (sighting in birdSightings) {
                                    val birdLatLng = LatLng(sighting.lat, sighting.lng)
                                    val birdMarker = MarkerOptions()
                                        .position(birdLatLng)
                                        .title(sighting.comName)
                                    mMap.addMarker(birdMarker)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<EBirdService.BirdSighting>>, t: Throwable) {
                        // Handle API call failure
                        // You can display an error message or handle it as needed
                    }
                })
        }

        override fun onMyLocationClick(location: Location) {
            Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
                .show()
        }

        override fun onMyLocationButtonClick(): Boolean {
            Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        companion object {
            const val REQUEST_LOCATION_PERMISSION = 1
        }
    }

