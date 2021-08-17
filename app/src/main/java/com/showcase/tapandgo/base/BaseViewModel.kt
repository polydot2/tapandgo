package com.showcase.tapandgo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    protected val _uiState by lazy { MutableSharedFlow<UiState>(replay = 1) }

    val uiState: SharedFlow<UiState>
        get() = _uiState

    fun initScreen() {
        viewModelScope.launch {
            _uiState.emit(UiState.Init)
        }
    }

    suspend fun success(uiModel: BaseUiModel) {
        _uiState.emit(UiState.Success(uiModel))
    }

    suspend fun error(appError: ApplicationError) {
        _uiState.emit(UiState.Error(appError))
    }

    suspend fun loading() {
        _uiState.emit(UiState.Loading)
    }
}
