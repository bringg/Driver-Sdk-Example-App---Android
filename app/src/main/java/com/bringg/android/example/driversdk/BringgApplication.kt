package com.bringg.android.example.driversdk

import android.app.Application
import android.util.Log
import driver_sdk.DriverSdkProvider
import driver_sdk.account.LoginCallback
import driver_sdk.account.LoginError
import driver_sdk.account.LoginMerchant
import driver_sdk.account.oidc.OpenIdConnectLoginConfig

class BringgApplication : Application() {

    private val TAG = "BringgApplication"

    override fun onCreate() {
        super.onCreate()

        DriverSdkProvider.init(this, ExampleNotificationProvider(this))
        val driverSdk = DriverSdkProvider.driverSdk()
        if (!driverSdk.isLoggedIn) {
            val email = "driver@m.com"
            val password = "1234"
            driverSdk.login.loginWithEmail(email, password, object : LoginCallback {
                override fun onLoginSuccess() {
                    Log.i(TAG, "onLoginSuccess")
                }

                override fun onLoginMultipleResults(p0: MutableList<LoginMerchant>) {
                    Log.i(TAG, "onLoginMultipleResults")
                    driverSdk.login.loginWithEmailAndMerchant(email, password, p0.first { it.name.contains("gil", true) }, this)
                }

                override fun onLoginFailed(p0: LoginError) {
                    Log.i(TAG, "onLoginFailed")
                }

                override fun onShouldLoginWithSSO(p0: OpenIdConnectLoginConfig) {
                    Log.i(TAG, "onShouldLoginWithSSO")
                }
            })
        }
    }
}