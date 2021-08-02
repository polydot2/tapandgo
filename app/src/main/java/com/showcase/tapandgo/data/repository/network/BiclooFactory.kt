package com.showcase.tapandgo.data.repository.network

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.showcase.tapandgo.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BiclooFactory {
    private val okHttpClient: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor()
        logger.level = BuildConfig.NETWORK_LOGGER_LEVEL

        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    fun create(
        moshi: Moshi,
        baseUrl: HttpUrl = BuildConfig.BICLOO_WEBSERVICE.toHttpUrl()
    ): BiclooService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(BiclooService::class.java)

}
