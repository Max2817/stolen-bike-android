package com.majateam.bikespot.welcome.viewmodel

import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.majateam.bikespot.ui.SingleLiveEvent

class WelcomeViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), LifecycleObserver {

    //val locationPermissionEventClick: SingleLiveEvent<Unit>()

    fun onNotificationSettingsClick(view: View) {
        //notificationButtonClickEvent.call()
    }
}