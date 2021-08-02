package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MainStands(
    @Json(name = "availabilities")
    val availabilities: Availabilities,
    @Json(name = "capacity")
    val capacity: Int
)