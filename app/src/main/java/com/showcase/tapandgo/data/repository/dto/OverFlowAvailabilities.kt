package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class OverFlowAvailabilities(
    @Json(name = "bikes")
    val bikes: Int,
    @Json(name = "stands")
    val stands: Int
) : Serializable