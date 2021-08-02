package com.showcase.tapandgo.data.di

import com.haroldadmin.cnradapter.NetworkResponse
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.base.ApplicationErrorType

fun NetworkResponse.ServerError<ApplicationError>.toApplicationError(): ApplicationError {
    return this.body?.let { it.apply { type = ApplicationErrorType.API } }
        ?: ApplicationError().apply { type = ApplicationErrorType.API }
}

fun NetworkResponse.NetworkError.toApplicationError(): ApplicationError {
    return ApplicationError(this.error.message).apply { type = ApplicationErrorType.NETWORK }
}

fun NetworkResponse.UnknownError.toApplicationError(): ApplicationError {
    return ApplicationError(this.error.message).apply { type = ApplicationErrorType.GENERIC }
}

fun <T : Any> NetworkResponse<T, ApplicationError>.safeResult(): T {
    return when (this) {
        is NetworkResponse.ServerError<ApplicationError> -> throw this.toApplicationError()
        is NetworkResponse.NetworkError -> throw this.toApplicationError()
        is NetworkResponse.UnknownError -> throw this.toApplicationError()
        is NetworkResponse.Success -> this.body
    }
}