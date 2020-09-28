package com.majateam.bikespot

import android.app.Application
import com.majateam.bikespot.crash.CrashReportingTree
import timber.log.Timber
import timber.log.Timber.DebugTree

class BikeSpotApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree());
        } else {
            Timber.plant(CrashReportingTree());
        }
    }
}