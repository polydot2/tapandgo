package com.showcase.tapandgo.base

data class ApplicationError(
    override val message: String? = "",
    var type: ApplicationErrorType = ApplicationErrorType.GENERIC
) : Throwable()