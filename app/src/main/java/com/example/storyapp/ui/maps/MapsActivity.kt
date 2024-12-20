package com.example.storyapp.ui.maps

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.StoriesResponse
import com.example.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("MapsActivity", "Map Ready")
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        showLoading(true)
        val token = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getString("access_token", null)

        if (token != null) {
            val client = ApiConfig.getApiService()
            client.getStoriesWithLocation("Bearer $token", 1)
                .enqueue(object : Callback<StoriesResponse> {
                    override fun onResponse(
                        call: Call<StoriesResponse>,
                        response: Response<StoriesResponse>
                    ) {
                        Log.d("MapsActivity", "Response: ${response.body()}")
                        showLoading(false)
                        if (response.isSuccessful) {
                            response.body()?.let { storyResponse ->
                                if (storyResponse.error == false) {
                                    addStoryMarkers(storyResponse)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                        showLoading(false)
                    }
                })
        }
    }

    private fun addStoryMarkers(response: StoriesResponse) {
        response.listStory.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder.include(latLng)
            }
        }

        try {
            val bounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        } catch (e: Exception) {
            // Handle case when no markers are added
            val defaultLocation = LatLng(-6.8957643, 107.6338462) // Bandung
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f)
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}