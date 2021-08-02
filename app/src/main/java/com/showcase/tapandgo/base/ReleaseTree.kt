package com.showcase.tapandgo.base

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN){
            // Then send error or warn logs on crashlytics for example (not implemented here)
        }
    }
}