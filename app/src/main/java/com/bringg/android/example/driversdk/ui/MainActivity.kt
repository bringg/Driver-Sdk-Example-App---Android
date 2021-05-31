package com.bringg.android.example.driversdk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.BuildConfig
import com.bringg.android.example.driversdk.ExampleNotificationProvider
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider
import driver_sdk.logging.BringgLog

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BringgSdkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriverSdkProvider.init(applicationContext, ExampleNotificationProvider(applicationContext))
        viewModel = ViewModelProvider(this, BringgSdkViewModel.Factory(DriverSdkProvider.driverSdk())).get(BringgSdkViewModel::class.java)

        // initialize bringg sdk
        if (BuildConfig.DEBUG) BringgLog.enableLogcatLog()

        setContentView(R.layout.main_activity)
    }
}
