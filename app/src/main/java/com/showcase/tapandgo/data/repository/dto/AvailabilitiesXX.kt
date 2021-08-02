package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvailabilitiesXX(
    @Json(name = "bikes")
    val bikes: Int,
    @Json(name = "stands")
    val stands: Int
)