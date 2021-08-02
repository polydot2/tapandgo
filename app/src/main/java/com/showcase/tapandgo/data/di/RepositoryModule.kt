package com.showcase.tapandgo.data.di

import com.showcase.tapandgo.data.repository.BiclooRepository
import com.showcase.tapandgo.data.repository.network.BiclooService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideBiclooRepository(
        service: BiclooService,
        dispatcher: CoroutineDispatcher
    ) = BiclooRepository(
        service, dispatcher
    )

    @Singleton
    @Provides
    fun providesDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
