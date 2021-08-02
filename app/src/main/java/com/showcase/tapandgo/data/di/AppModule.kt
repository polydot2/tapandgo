package com.showcase.tapandgo.data.di

import android.content.Context
import com.showcase.tapandgo.base.TapAndGoApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): TapAndGoApplication {
        return app as TapAndGoApplication
    }
}