package com.showcase.tapandgo.data.di

import com.showcase.tapandgo.data.repository.network.BiclooFactory
import com.showcase.tapandgo.data.repository.network.BiclooService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitClient(moshi: Moshi): BiclooService = BiclooFactory().create(moshi)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

}