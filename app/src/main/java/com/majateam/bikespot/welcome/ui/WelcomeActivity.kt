package com.majateam.bikespot.welcome.ui

import android.os.Bundle
import com.majateam.bikespot.R
import com.majateam.bikespot.base.ActivityBindingProperty
import com.majateam.bikespot.databinding.ActivityWelcomeBinding
import com.majateam.bikespot.welcome.viewmodel.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : PermissionActivity() {

    private val binding by ActivityBindingProperty<ActivityWelcomeBinding>(R.layout.activity_welcome)
    val model: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = model

        binding.executePendingBindings()

        welcome_permission_location_button.
    }

}
