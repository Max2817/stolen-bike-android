package com.majateam.bikespot.extension

import android.Manifest
import com.majateam.bikespot.R

sealed class AppPermission(
    val permissionName: String, val requestCode: Int, val deniedMessageId: Int, val explanationMessageId: Int
) {
    companion object {
        val permissions: List<AppPermission> by lazy {
            listOf(
                AccessFineLocation
            )
        }
    }

    object AccessFineLocation : AppPermission(Manifest.permission.ACCESS_FINE_LOCATION, 42,
        R.string.permission_required_text, R.string.permission_required_text
    )
}