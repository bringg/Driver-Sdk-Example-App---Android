package com.bringg.android.example.driversdk.authentication

import driver_sdk.account.LoginMerchant
import driver_sdk.models.enums.LoginResult

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val result: LoginResult,
    val merchants: List<LoginMerchant> = emptyList()
)
