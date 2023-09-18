package com.example.birdview

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.birdview.databinding.ActivityMapV2Binding
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.birdview.databinding.ActivityBirdEntryBinding
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


//Interface for ebirdservice
interface EBirdService {
    //Will change endpoint to a more user sentric on for sure
    //Forgot too call the birdsighting method in the map ready method
    //I'll implement UI threading so the baby doesnt have too load for too long
    @GET("/v2/data/obs/US/recent")
    fun getBirdSightings(@Query("key") apiKey: String): Call<List<BirdSighting>> //data class

    //the Data class
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
   //Class for most stuff
    //Run on UI thread too boost performance

    class MapV2 : AppCompatActivity(), OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {


       private lateinit var mMap: GoogleMap
       lateinit var toggle: ActionBarDrawerToggle
       private lateinit var binding: ActivityMapV2Binding
       private lateinit var eBirdService: EBirdService
       private lateinit var fusedLocationClient: FusedLocationProviderClient
       private var userLocation: LatLng? = null



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
               // requestLocation()
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

       //Apply Run on UI threads here
       // The brain
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
           //Solution? Yes it was

           userLocation?.let {
               getBirdSightings(it)
           }
               ?: run {
                   //
                   //will implement -- Changes
               }
           //getBirdSightings(userLocation)
           requestLocation()
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
               return
           }
           fusedLocationClient.lastLocation
               .addOnSuccessListener { location: Location? ->
                   location?.let {
                       userLocation = LatLng(it.latitude, it.longitude)
                       getBirdSightings(userLocation!!)
                   }
               }
       }

       private fun getBirdSightings(userLocation: LatLng) {

           val apiKey = "9riis08rlgc2" //  eBird API key

           eBirdService.getBirdSightings(apiKey)
               .enqueue(object : Callback<List<EBirdService.BirdSighting>> {
                   override fun onResponse(
                       call: Call<List<EBirdService.BirdSighting>>,
                       response: Response<List<EBirdService.BirdSighting>>
                   ) {
                       if (response.isSuccessful) {
                           val birdSightings = response.body()

                           if (birdSightings != null) {
                               for (sighting in birdSightings) {
                                   val birdLatLng = LatLng(sighting.lat, sighting.lng)
                                   val birdMarker = MarkerOptions()
                                       .position(birdLatLng)
                                       .title(sighting.comName)

                                   // Add marker to map
                                   val marker = mMap.addMarker(birdMarker)

                                   // Attach sighting data to the marker as a tag
                                   if (marker != null) {
                                       marker.setTag(sighting)
                                   }
                               }
                           }
                       }
                   }

                   override fun onFailure(
                       call: Call<List<EBirdService.BirdSighting>>,
                       t: Throwable
                   ) {
                       // Handle API call failure
                       // You can display an error message or handle it as needed
                   }
               })

           // Set a click listener for the markers
           mMap.setOnMarkerClickListener { marker ->
               // Retrieve the sighting data from the marker's tag
               val sighting = marker.tag as? EBirdService.BirdSighting
               sighting?.let {
                   navigateToBirdSighting(it)
               }

               // Return 'false' to allow default behavior (opening marker info window)
               false
           }
       }
       private fun navigateToBirdSighting(sighting: EBirdService.BirdSighting) {
           val context = GeoApiContext.Builder()
                   //Changed second version of the API that includes Google Directions Api and Maps API
               .apiKey("AIzaSyAcnPpN2K87hgK2IIjDyqReQlIPjU41kvo")
               .build()

           val request = DirectionsApi.newRequest(context)
               .origin(userLocation!!.latitude.toString() + "," + userLocation!!.longitude.toString())
               .destination(sighting.lat.toString() + "," + sighting.lng.toString())
               .mode(TravelMode.DRIVING)

           Thread {
               try {
                   val result = request.await()
                   val route = result.routes[0]

                   runOnUiThread {
                       val polylineOptions = PolylineOptions()

                       for (leg in route.legs) {
                           for (step in leg.steps) {
                               val points = step.polyline.decodePath()

                               for (point in points) {
                                   polylineOptions.add(LatLng(point.lat, point.lng))
                               }
                           }
                       }

                       mMap.addPolyline(polylineOptions)
                   }
               } catch (e: Exception) {
                   Log.e("DirectionsAPI", "Failed to get directions: ${e.message}")
               }
           }.start()
       }

       override fun onMyLocationClick(location: Location) {
           //userLocation = LatLng(location.latitude, location.longitude)
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




