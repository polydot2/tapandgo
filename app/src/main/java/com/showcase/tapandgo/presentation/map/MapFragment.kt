package com.showcase.tapandgo.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.showcase.tapandgo.databinding.ViewDialogInputBinding
import com.showcase.tapandgo.presentation.map.cluster.ClusterRenderer
import com.showcase.tapandgo.presentation.map.cluster.MarkerClusterItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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
    private lateinit var clusterManager: ClusterManager<MarkerClusterItem>

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
        initSmartDestinationButton()
        fetchStations()
    }

    private fun initSmartDestinationButton() {
        permissionRequest.send()
        binding.smartDestination.setOnClickListener { openDialog() }
    }

    private fun initMap(googleMap: GoogleMap) {
        map = googleMap
        map.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = false
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        }

        clusterManager = ClusterManager(requireContext(), map)
        clusterManager.apply {
            renderer = ClusterRenderer(requireContext(), map, this)
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
    }

    private fun onLongMapClick(latLng: LatLng) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).firstOrNull()
        openDialog(address?.getAddressLine(0))
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

    private fun onError(error: ApplicationError) {
        Toast.makeText(requireContext(), error.message, LENGTH_SHORT).show()
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
                viewModel.currentPosition = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }

    private fun openDialog(prefill: String? = null) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Votre destination")
        val view = ViewDialogInputBinding.inflate(layoutInflater, null, false)
        prefill?.let { view.inputText.setText(it) }
        builder.setView(view.root)
        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = view.inputText.text.toString()
            if (inputText.isBlank()) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                binding.bottomSheet.hide()
                computeSmartDestination(inputText)
            }
        }
        builder.show()
    }

    private fun computeSmartDestination(inputToSearch: String?) {
        inputToSearch?.let { input ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocationName(input, 1).firstOrNull()
            address?.let {
                viewModel.destination = LatLng(address.latitude, address.longitude)

                val navigate = MapFragmentDirections.actionMapFragmentToDetailsFragment(
                    viewModel.currentPosition!!,
                    viewModel.destination!!,
                    viewModel.findNearestStand()!!,
                    viewModel.findNearestDestinationStand()!!
                )

                findNavController().navigate(navigate)
            } ?: run {
                Toast.makeText(requireContext(), "Aucune d'adresse trouvée", LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onPermissionsResult(result: List<PermissionStatus>) {
        if (result.allGranted()) {
            centerOnUserLocation()
        }
    }
}
