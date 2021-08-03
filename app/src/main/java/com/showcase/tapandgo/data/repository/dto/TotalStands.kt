package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class TotalStands(
    @Json(name = "availabilities")
    val availabilities: TotalStandAvailabilities,
    @Json(name = "capacity")
    val capacity: Int
) : Serializable