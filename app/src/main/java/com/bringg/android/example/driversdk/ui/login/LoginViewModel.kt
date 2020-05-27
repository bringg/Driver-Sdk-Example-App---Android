package com.bringg.android.example.driversdk.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider
import driver_sdk.account.LoginCallback
import driver_sdk.account.LoginError
import driver_sdk.account.LoginMerchant
import driver_sdk.account.oidc.OpenIdConnectLoginConfig

class LoginViewModel() : ViewModel() {

    private val bringgLoginApi = DriverSdkProvider.driverSdk().login

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {

        bringgLoginApi.loginWithEmail(username, password, object : LoginCallback {
            override fun onLoginSuccess() {
                _loginResult.value = LoginResult(driver_sdk.models.enums.LoginResult.SUCCESS)
            }

            override fun onLoginMultipleResults(userMerchants: List<LoginMerchant>) {
                _loginResult.value = LoginResult(driver_sdk.models.enums.LoginResult.FAILED_TO_LOGIN, userMerchants)
            }

            override fun onLoginFailed(p0: LoginError) {
                _loginResult.value = LoginResult(driver_sdk.models.enums.LoginResult.FAILED_TO_LOGIN)
            }

            override fun onShouldLoginWithSSO(p0: OpenIdConnectLoginConfig) {
                _loginResult.value = LoginResult(driver_sdk.models.enums.LoginResult.FAILED_TO_LOGIN)
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
