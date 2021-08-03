package com.showcase.tapandgo.data.repository.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Station(
    @Json(name = "address")
    val address: String,
    @Json(name = "banking")
    val banking: Boolean,
    @Json(name = "bonus")
    val bonus: Boolean,
    @Json(name = "connected")
    val connected: Boolean,
    @Json(name = "contractName")
    val contractName: String,
    @Json(name = "lastUpdate")
    val lastUpdate: String,
    @Json(name = "mainStands")
    val mainStands: MainStands,
    @Json(name = "name")
    val name: String,
    @Json(name = "number")
    val number: Int,
    @Json(name = "overflow")
    val overflow: Boolean,
    @Json(name = "overflowStands")
    val overflowStands: OverflowStands?,
    @Json(name = "position")
    val position: Position,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalStands")
    val totalStands: TotalStands
) : Serializable