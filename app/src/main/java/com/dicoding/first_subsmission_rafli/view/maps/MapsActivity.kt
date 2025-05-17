package com.dicoding.first_subsmission_rafli.view.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.first_subsmission_rafli.R
import com.dicoding.first_subsmission_rafli.Result.StoryResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.first_subsmission_rafli.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val mapViewModel: MapsViewModel by viewModels()
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun getCurrentLocation() {
        val Locate = LatLng(-7.9812, 112.6207)


        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val description = sharedPreferences.getString("DESCRIPTION", "No Description")


        mMap.addMarker(
            MarkerOptions()
                .position(Locate)
                .title("Your Locations")
                .snippet(description)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Locate, 15f))
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style)
            )
            if (!success) {
                showToast("Failed to apply map style.")
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        getCurrentLocation()

        mapViewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is StoryResult.Success -> {
                    val latLngBoundsBuilder = LatLngBounds.Builder()
                    result.data.forEach { story ->
                        val latLng = LatLng(story.lat, story.lon)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                        )
                        latLngBoundsBuilder.include(latLng)
                    }

                    if (result.data.isNotEmpty()) {
                        val bounds = latLngBoundsBuilder.build()
                        val padding = 100 // Padding untuk margin
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                    }
                }

                is StoryResult.Error -> {
                    showToast("Failed to load stories with location")
                }

                is StoryResult.Loading -> {

                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}