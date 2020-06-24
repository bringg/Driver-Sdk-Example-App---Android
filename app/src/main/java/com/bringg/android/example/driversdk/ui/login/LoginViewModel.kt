package com.bringg.android.example.driversdk.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider
import driver_sdk.account.LoginMerchant
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.DriverLoginResult
import driver_sdk.driver.model.result.PhoneVerificationRequestResult
import driver_sdk.driver.model.result.ResetPasswordRequestResult

class LoginViewModel() : ViewModel() {

    private val TAG = "LoginViewModel"

    private val bringgAuthentication = DriverSdkProvider.driverSdk().authentication

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<DriverLoginResult?>()
    val loginResult: LiveData<DriverLoginResult?> = _loginResult

    fun loginWithEmail(email: String, password: String) {
        bringgAuthentication.loginWithEmail(email, password, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithPhone(phone: String, verificationCode: String) {
        bringgAuthentication.loginWithPhone(phone, verificationCode, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithTokenAndSecret(region: String, token: String, secret: String) {
        bringgAuthentication.loginWithTokenAndSecret(region, token, secret, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithEmail(email: String, password: String, merchant: LoginMerchant) {
        bringgAuthentication.loginWithEmail(email, password, merchant, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithPhone(phone: String, verificationCode: String, merchant: LoginMerchant) {
        bringgAuthentication.loginWithPhone(phone, verificationCode, merchant, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithOIDCToken(region: String, token: String) {
        bringgAuthentication.loginWithOIDCToken(region, token, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun requestPhoneVerification(phone: String) {
        bringgAuthentication.requestPhoneVerification(phone, object : ResultCallback<PhoneVerificationRequestResult> {
            override fun onResult(result: PhoneVerificationRequestResult) {
                if (result.success) {
                    Log.i(
                        TAG, "phone verification request sent to Bringg." +
                                "SMS verification code will be sent to the provided phone number." +
                                "Use the verification code from the SMS message to call DriverSdkProvider.driverSdk.loginWithPhone(phone, verificationCode, callback)"
                    )
                } else {
                    Log.i(TAG,"phone verification request failed")
                }
            }
        })
    }

    fun requestResetPasswordLink(email: String) {
        bringgAuthentication.requestResetPasswordLink(email, object : ResultCallback<ResetPasswordRequestResult> {
            override fun onResult(result: ResetPasswordRequestResult) {
                if (result.success) {
                    Log.i(
                        TAG, "reset password request sent to Bringg." +
                                "A link to reset the password will be sent to the provided email address" +
                                "Use the password to call DriverSdkProvider.driverSdk.loginWithEmail(email, password, callback)"
                    )
                } else {
                    Log.i(TAG,"reset password request failed")
                }
            }
        })
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }
}
