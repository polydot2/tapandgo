package com.showcase.tapandgo.presentation.map

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.BaseUiModel
import com.showcase.tapandgo.base.BaseViewModel
import com.showcase.tapandgo.base.UiState
import com.showcase.tapandgo.data.repository.BiclooRepository
import com.showcase.tapandgo.data.repository.dto.Position
import com.showcase.tapandgo.data.repository.dto.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: BiclooRepository
) : BaseViewModel() {

    var initialPosition: LatLng = LatLng(47.2173, -1.5534)
    var initialZoom: Float = 12f
    var currentPosition: LatLng? = null
    var destination: LatLng? = null
    var stations: List<Station> = listOf()

    fun retrieveBiclooLocations() {
        viewModelScope.launch(dispatcher) {
            repository.getStationsByContract()
                .onStart {
                    _uiState.emit(UiState.Loading)
                }.catch { e ->
                    Timber.e(e)
                    _uiState.emit(UiState.Error(ApplicationError("Oups something went wrong")))
                }
                .collect    {
                    stations = it
                    _uiState.emit(UiState.Success(MapFragmentUiModel(it)))
                }
        }
    }

    fun findNearestStand(): Station? {
        return currentPosition?.let { userPosition ->
            // at least 1 bike, nearest to the current user location
            stations.filter { it.mainStands.availabilities.bikes > 0 }
                .minByOrNull { distanceFromCurrentLocation(userPosition, it.position) }
        }
    }

    fun findNearestDestinationStand(): Station? {
        return destination?.let { destinationPosition ->
            // at least 1 stand free, nearest to the current user location
            stations.filter { it.mainStands.availabilities.bikes > 0 }
                .minByOrNull { distanceFromCurrentLocation(destinationPosition, it.position) }
        }
    }

    private fun distanceFromCurrentLocation(userPosition: LatLng, stationPosition: Position): Float {
        val locationA = Location("").apply {
            latitude = userPosition.latitude
            longitude = userPosition.longitude
        }

        val locationB = Location("").apply {
            latitude = stationPosition.latitude
            longitude = stationPosition.longitude
        }

        return locationA.distanceTo(locationB)
    }
}

data class MapFragmentUiModel(val stations: List<Station>) : BaseUiModel()
