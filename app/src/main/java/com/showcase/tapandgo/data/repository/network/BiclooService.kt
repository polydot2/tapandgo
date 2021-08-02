package com.showcase.tapandgo.data.repository.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.showcase.tapandgo.base.ApplicationError
import com.showcase.tapandgo.data.repository.dto.Station
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BiclooService {

    @GET("stations")
    suspend fun getStationsByContract(
        @Query("contract") contract: String,
        @Query("apiKey") apiKey: String
    ): NetworkResponse<List<Station>, ApplicationError>

    @GET("stations/{stationNumber}")
    suspend fun getStationDetails(
        @Path("stationNumber") stationNumber: String,
        @Query("contract") contract: String,
        @Query("apiKey") apiKey: String
    ): NetworkResponse<Station, ApplicationError>
}