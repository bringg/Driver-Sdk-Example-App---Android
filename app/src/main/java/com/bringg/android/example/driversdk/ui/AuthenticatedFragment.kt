package com.bringg.android.example.driversdk.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.BuildConfig
import com.bringg.android.example.driversdk.ExampleNotificationProvider
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.ui.login.LoginFragment
import driver_sdk.DriverSdkProvider
import driver_sdk.logging.BringgLog

open class AuthenticatedFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DriverSdkProvider.init(context, ExampleNotificationProvider(context))
        if (BuildConfig.DEBUG) BringgLog.enableLogcatLog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.IS_LOGGED_IN)
            .observe(currentBackStackEntry, { success ->
                if (!success) {
                    val startDestination = navController.graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        DriverSdkProvider.driverSdk().data.login.observe(viewLifecycleOwner, { isLoggedIn ->
            if (!isLoggedIn) {
                navController.navigate(R.id.login_action)
            }
        })
    }
}
