package com.showcase.tapandgo.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.BaseFragment
import com.showcase.tapandgo.base.UiState
import com.showcase.tapandgo.data.repository.dto.Station
import com.showcase.tapandgo.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding>(R.layout.fragment_map),
    OnMapReadyCallback {

    private val permissionRequest by lazy {
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build()
    }

    override val viewModel: MapViewModel by viewModels()

    private lateinit var map: GoogleMap
    private var clusterManager: ClusterManager<MarkerClusterItem>? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onInit() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        initMap(googleMap)

        // if permissions are granted, center on user location now
        onPermissionsResult(permissionRequest.checkStatus())
        initCenterOnUserButton()
        fetchStations()
    }

    private fun initMap(googleMap: GoogleMap) {
        map = googleMap
        map.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = false
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        }

        clusterManager = ClusterManager(requireContext(), map)
        clusterManager?.apply {
            renderer = MarkerClusterRenderer(requireContext(), map, clusterManager)
            setOnClusterClickListener { onClusterClick(it) }
            setOnClusterItemClickListener { onClusterItemClick(it) }
        }

        map.apply {
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
            setOnMapClickListener { onMapClick() }
            setOnCameraMoveStartedListener {
                when (it) {
                    GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> onMapClick()
                }
            }
        }
    }

    private fun onMapClick() {
        binding.bottomSheet.hide()
    }

    private fun onClusterItemClick(markerClusterItem: MarkerClusterItem): Boolean {
        binding.bottomSheet.show(markerClusterItem.station)
        return false
    }

    private fun onClusterClick(cluster: Cluster<MarkerClusterItem>): Boolean {
        return false
    }

    private fun onSuccess(uiModel: Any) {
        (uiModel as? MapFragmentUiModel)?.let {
            addMarkerOnMap(it.stations)
        }
    }

    private fun addMarkerOnMap(stations: List<Station>) {
        for (station in stations) {
            val clusterItem = MarkerClusterItem(
                LatLng(station.position.latitude, station.position.longitude),
                station
            )
            clusterManager?.addItem(clusterItem)
            clusterManager?.cluster()
        }
    }

    private fun onError(error: ApplicationError) {
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }

    private fun onLoading() {
    }

    private fun fetchStations() {
        viewModel.uiState.observe(viewLifecycleOwner, {
            when (it) {
                is UiState.Init -> onInit()
                is UiState.Loading -> onLoading()
                is UiState.Error -> onError(it.error)
                is UiState.Success -> onSuccess(it.uiModel)
            }
        })

        viewModel.retrieveBiclooLocations()
    }

    private fun initCenterOnUserButton() {
        permissionRequest.addListener { onPermissionsResult(it) }
        binding.centerOnUser.setOnClickListener {
            permissionRequest.send()
            binding.bottomSheet.hide()
        }
    }

    @SuppressLint("MissingPermission")
    private fun centerOnUserLocation() {
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        16f
                    )
                )
            }
        }
    }

    private fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            //result.anyPermanentlyDenied() -> context.showPermanentlyDeniedDialog(result)
            //result.anyShouldShowRationale() -> context.showRationaleDialog(result, request)
            result.allGranted() -> centerOnUserLocation()
        }
    }
}
