package com.showcase.tapandgo.presentation.destination

import android.content.Context
import android.location.Geocoder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.BaseFragment
import com.showcase.tapandgo.base.BaseUiModel
import com.showcase.tapandgo.data.repository.dto.Position
import com.showcase.tapandgo.databinding.FragmentDestinationBinding
import com.showcase.tapandgo.presentation.map.cluster.ClusterRenderer
import com.showcase.tapandgo.presentation.map.cluster.MarkerClusterItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class DestinationFragment :
    BaseFragment<DestinationViewModel, FragmentDestinationBinding, BaseUiModel>(R.layout.fragment_destination), OnMapReadyCallback {

    companion object {
        // map padding on center bound
        private const val PADDING: Int = 128
    }

    override val viewModel: DestinationViewModel by viewModels()
    private val arguments: DestinationFragmentArgs by navArgs()

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<MarkerClusterItem>

    override fun onInit() {
        initMap()
        initDetails()
        initButton()
    }

    private fun initButton() {
        binding.googleMapItinerary.setOnClickListener {
            startActivity(viewModel.getGoogleMapIntent(arguments))
        }
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        initSettingMap(googleMap)
        addMarkers()
    }

    private fun initSettingMap(googleMap: GoogleMap) {
        map = googleMap
        map.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = false
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        }

        clusterManager = ClusterManager(requireContext(), map)
        clusterManager.apply {
            renderer = ClusterRenderer(requireContext(), map, clusterManager, false)
        }
    }

    private fun addMarkers() {
        val markers = mapOf(
            "" to arguments.from,
            "A" to arguments.stationDeparture.position.toLatLng(),
            "B" to arguments.stationsArrival.position.toLatLng(),
            "C" to arguments.to
        )

        markers.forEach { clusterManager.addItem(MarkerClusterItem(latLng = it.value, step = it.key)) }
        clusterManager.cluster()
        centerMap(markers)
    }

    private fun centerMap(markers: Map<String, LatLng>) {
        val builder = LatLngBounds.Builder()
        markers.forEach { builder.include(it.value) }
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, PADDING)
        map.animateCamera(cu)
    }

    private fun initDetails() {
        binding.apply {
            departure.text = arguments.from.toLocationName(requireContext())
            arrival.text = arguments.to.toLocationName(requireContext())
            pickupStation.text = arguments.stationDeparture.name
            dropoffStation.text = arguments.stationsArrival.name
        }
    }

    override fun onSuccess(uiModel: BaseUiModel) {
    }

    override fun onError(error: ApplicationError) {
    }
}

private fun Position.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
private fun LatLng.toLocationName(context: Context): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return geocoder.getFromLocation(this.latitude, this.longitude, 1).firstOrNull()?.getAddressLine(0)
}