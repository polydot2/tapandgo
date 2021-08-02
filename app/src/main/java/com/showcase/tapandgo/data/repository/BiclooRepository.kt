package com.showcase.tapandgo.data.repository

import com.showcase.tapandgo.BuildConfig
import com.showcase.tapandgo.data.di.safeResult
import com.showcase.tapandgo.data.repository.dto.Station
import com.showcase.tapandgo.data.repository.network.BiclooService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BiclooRepository(
    private val service: BiclooService,
    private val dispatcher: CoroutineDispatcher
) {
    private val apiKey = BuildConfig.BICLOO_API_KEY
    private val contract = "nantes"

    suspend fun getStationsByContract(): Flow<List<Station>> =
        flow {
            emit(
                service.getStationsByContract(contract, apiKey).safeResult()
            )
        }.flowOn(dispatcher)

    suspend fun getStationDetails(stationNumber: String): Flow<Station> =
        flow {
            emit(
                service.getStationDetails(
                    stationNumber, contract, apiKey
                ).safeResult()
            )
        }.flowOn(dispatcher)
}
