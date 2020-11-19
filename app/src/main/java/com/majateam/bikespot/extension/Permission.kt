package com.majateam.bikespot.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment

fun Fragment.isGranted(permission: AppPermission) = run {
    context?.let {
        (PermissionChecker.checkSelfPermission(it, permission.permissionName
        ) == PermissionChecker.PERMISSION_GRANTED)
    } ?: false
}

fun Fragment.shouldShowRationale(permission: AppPermission) = run {
    shouldShowRequestPermissionRationale(permission.permissionName)
}

fun Fragment.requestPermission(permission: AppPermission) {
    requestPermissions(arrayOf(permission.permissionName), permission.requestCode
    )
}

fun AppCompatActivity.checkPermission(permission: AppPermission) = run {
    this.let {
        (ActivityCompat.checkSelfPermission(it, permission.permissionName
        ) == PermissionChecker.PERMISSION_GRANTED)
    } ?: false
}

fun AppCompatActivity.shouldRequestPermissionRationale(permission: AppPermission) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)

fun AppCompatActivity.requestAllPermissions(permission: AppPermission) {
    ActivityCompat.requestPermissions(this, arrayOf(permission.permissionName), permission.requestCode)
}