package com.bringg.android.example.driversdk.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.ExampleNotificationProvider
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider

class WelcomeFragment : Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        DriverSdkProvider.init(context.applicationContext, ExampleNotificationProvider(context))
        DriverSdkProvider.driverSdk().data.login().observeForever { isLoggedIn ->
            val navController = findNavController()
            if (isLoggedIn) {
                navController.navigate(R.id.task_list_fragment)
            } else {
                navController.navigate(R.id.login_fragment)
            }
        }
    }
}