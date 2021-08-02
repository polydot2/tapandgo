package com.showcase.tapandgo.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
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
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.BaseFragment
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onInit() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this);
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = false

        // if permissions are granted, center on user location now
        onPermissionsResult(permissionRequest.checkStatus())

        initCenterOnUserButton()
        initMarkers()
    }

    private fun initMarkers() {
        //it.setOnMarkerClickListener(this)

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        map?.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        map?.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    private fun initCenterOnUserButton() {
        permissionRequest.addListener { onPermissionsResult(it) }
        binding.findUser.setOnClickListener {
            permissionRequest.send()
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
                        12f
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
