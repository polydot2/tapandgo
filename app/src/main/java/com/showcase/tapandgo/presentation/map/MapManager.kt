package com.showcase.tapandgo.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.showcase.tapandgo.R
import com.showcase.tapandgo.data.repository.dto.Station
import com.showcase.tapandgo.presentation.map.cluster.ClusterRenderer
import com.showcase.tapandgo.presentation.map.cluster.MarkerClusterItem
import java.util.*

class MapManager(private var context: Context, private var map: GoogleMap, private var initialPosition: LatLng, private var initialZoom: Float, private var mapListener: MapListener) {
    companion object {
        // map padding on center bound
        private const val PADDING: Int = 128
    }

    var currentPosition: LatLng? = null
    private var clusterManager: ClusterManager<MarkerClusterItem>
    private var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null

    init {
        map.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = false
            setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        }
        clusterManager = ClusterManager(context, map)
        clusterManager.apply {
            renderer = ClusterRenderer(context, map, this)
            setOnClusterClickListener { onClusterClick(it) }
            setOnClusterItemClickListener { onClusterItemClick(it) }
        }
        map.apply {
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
            setOnMapClickListener { onMapClick() }
            setOnMapLongClickListener { latLng -> onLongMapClick(latLng) }
            setOnCameraMoveStartedListener {
                when (it) {
                    GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> onMapClick()
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun onClusterClick(cluster: Cluster<MarkerClusterItem>): Boolean {
        val builder = LatLngBounds.builder()
        cluster.items.forEach {
            builder.include(LatLng(it.latLng.latitude, it.latLng.longitude))
        }
        val bounds = builder.build()

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, PADDING))

        return true
    }

    fun addMarkerOnMap(stations: List<Station>) {
        map.clear()

        for (station in stations) {
            val clusterItem = MarkerClusterItem(
                LatLng(station.position.latitude, station.position.longitude),
                station
            )
            clusterManager.addItem(clusterItem)
            clusterManager.cluster()
        }
    }

    private fun onLongMapClick(latLng: LatLng) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).firstOrNull()
        mapListener.onLongMapClickCallBack(address?.getAddressLine(0))
    }

    private fun onMapClick() {
        mapListener.onMapClick()
    }

    private fun onClusterItemClick(markerClusterItem: MarkerClusterItem): Boolean {
        mapListener.onClusterItemClick(markerClusterItem)
        return false
    }

    @SuppressLint("MissingPermission")
    fun centerOnUserLocation() {
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lastLocation = location
                currentPosition = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16f))
            }
        }
    }

    fun centerOnCity() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, initialZoom))
    }
}

abstract class MapListener {
    abstract fun onLongMapClickCallBack(addressLine: String?)
    abstract fun onMapClick()
    abstract fun onClusterItemClick(markerClusterItem: MarkerClusterItem)
}