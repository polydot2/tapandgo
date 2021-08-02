package com.showcase.tapandgo.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    protected val _uiState by lazy { MediatorLiveData<UiState>() }

    val uiState: LiveData<UiState>
        get() = _uiState

    fun initScreen() {
        _uiState.postValue(UiState.Init)
    }

    fun displayError(appError: ApplicationError) {
        _uiState.postValue(UiState.Error(appError))
    }
}
