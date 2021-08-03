package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class MainStands(
    @Json(name = "availabilities")
    val availabilities: StandAvailabilities,
    @Json(name = "capacity")
    val capacity: Int
) : Serializable