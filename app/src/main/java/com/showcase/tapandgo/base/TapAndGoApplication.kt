package com.showcase.tapandgo.base

import android.app.Application
import com.showcase.tapandgo.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TapAndGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDebugConfiguration()
    }

    private fun initDebugConfiguration() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.i("Enabling Debug")
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}
