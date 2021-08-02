package com.showcase.tapandgo.presentation.map

import androidx.lifecycle.liveData
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.BaseUiModel
import com.showcase.tapandgo.base.BaseViewModel
import com.showcase.tapandgo.base.UiState
import com.showcase.tapandgo.data.repository.BiclooRepository
import com.showcase.tapandgo.data.repository.dto.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: BiclooRepository
) : BaseViewModel() {

    fun retrieveBiclooLocations() {
        _uiState.addSource(liveData(dispatcher) {
            repository.getStationsByContract()
                .onStart {
                    emit(UiState.Loading)
                }.catch { e ->
                    Timber.e(e)
                    emit(UiState.Error(ApplicationError("Oups something went wrong")))
                }
                .collect {
                    emit(UiState.Success(MapFragmentUiModel(it)))
                }
        }) {
            _uiState.postValue(it)
        }
    }
}

data class MapFragmentUiModel(val stations: List<Station>) : BaseUiModel()
