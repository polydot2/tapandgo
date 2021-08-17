package com.showcase.tapandgo.presentation.map

import android.Manifest
import android.app.AlertDialog
import android.location.Geocoder
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.showcase.tapandgo.R
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.BaseFragment
import com.showcase.tapandgo.databinding.FragmentMapBinding
import com.showcase.tapandgo.databinding.ViewDialogInputBinding
import com.showcase.tapandgo.presentation.map.cluster.MarkerClusterItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MapFragmentUiModel>(R.layout.fragment_map), OnMapReadyCallback {

    override val viewModel: MapViewModel by viewModels()

    private lateinit var mapManager: MapManager

    private val permissionRequest by lazy {
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build()
    }

    override fun onInit() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        initMap(googleMap)
        initCenterOnUserButton()
        initSmartDestinationButton()
        fetchStations()

        // if permissions are granted, center on user location now
        if (permissionRequest.checkStatus().allGranted()) {
            mapManager.centerOnUserLocation()
        } else {
            mapManager.centerOnCity()
        }
    }

    private fun fetchStations() {
        viewModel.retrieveBiclooLocations()
    }

    private fun initSmartDestinationButton() {
        binding.smartDestination.setOnClickListener {
            if (permissionRequest.checkStatus().allGranted()) {
                openDestinationDialog()
            } else {
                permissionRequest.send()
            }
        }
    }

    private fun initMap(googleMap: GoogleMap) {
        mapManager = MapManager(requireContext(), googleMap, viewModel.initialPosition, viewModel.initialZoom, object : MapListener() {
            override fun onLongMapClickCallBack(addressLine: String?) {
                openDestinationDialog(addressLine)
            }

            override fun onMapClick() {
                binding.bottomSheet.hide()
            }

            override fun onClusterItemClick(markerClusterItem: MarkerClusterItem) {
                binding.bottomSheet.show(markerClusterItem.station)
            }
        })
    }

    private fun initCenterOnUserButton() {
        permissionRequest.addListener { onPermissionsResult(it) }
        binding.centerOnUser.setOnClickListener {
            permissionRequest.send()
            binding.bottomSheet.hide()
        }
    }

    private fun openDestinationDialog(prefill: String? = null) {
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
                viewModel.currentPosition = mapManager.currentPosition
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
            mapManager.centerOnUserLocation()
        } else {
            mapManager.centerOnCity()
        }
    }

    override fun onSuccess(uiModel: MapFragmentUiModel) {
        (uiModel as? MapFragmentUiModel)?.let {
            mapManager.addMarkerOnMap(it.stations)
        }
    }

    override fun onError(error: ApplicationError) {
        Toast.makeText(requireContext(), error.message, LENGTH_SHORT).show()
    }
}
