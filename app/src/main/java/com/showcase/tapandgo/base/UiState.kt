package com.showcase.tapandgo.base

sealed class UiState {
    object Init : UiState()
    object Loading : UiState()
    data class Error(val error: ApplicationError) : UiState()
    data class Success(val uiModel: BaseUiModel) : UiState()
}
