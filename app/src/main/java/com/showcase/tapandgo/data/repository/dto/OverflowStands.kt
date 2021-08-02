package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverflowStands(
    @Json(name = "availabilities")
    val availabilities: AvailabilitiesX,
    @Json(name = "capacity")
    val capacity: Int
)