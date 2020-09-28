package com.majateam.bikespot.crash

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import timber.log.Timber.Tree
import timber.log.Timber.log

class CrashReportingTree : Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        FirebaseCrashlytics.getInstance().log(message)
    }
}