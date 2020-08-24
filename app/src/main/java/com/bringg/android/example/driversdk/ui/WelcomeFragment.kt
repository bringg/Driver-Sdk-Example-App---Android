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

        // 1. initialize the sdk:
        DriverSdkProvider.init(context.applicationContext, ExampleNotificationProvider(context))
        // 2. get the sdk instance:
        val driverSdk = DriverSdkProvider.driverSdk()
        // 3. use the sdk instance:
        val isLoggedIn = driverSdk.isLoggedIn

        showNextFragment(isLoggedIn)
    }

    private fun showNextFragment(isLoggedIn: Boolean) {
        val navController = findNavController()
        if (isLoggedIn) {
            navController.navigate(R.id.task_list_fragment)
        } else {
            navController.navigate(R.id.login_fragment)
        }
    }
}