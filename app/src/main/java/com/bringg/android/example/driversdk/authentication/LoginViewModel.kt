package com.bringg.android.example.driversdk.authentication

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.R
import driver_sdk.account.LoginMerchant
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.DriverLoginResult

class LoginViewModel(private val bringgSdkViewModel: BringgSdkViewModel) : ViewModel() {

    //region factory
    class Factory(private val bringgSdkViewModel: BringgSdkViewModel) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = LoginViewModel(bringgSdkViewModel) as T
    }
    //endregion

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<DriverLoginResult?>()
    val loginResult: LiveData<DriverLoginResult?> = _loginResult

    fun loginWithEmail(email: String, password: String) {
        bringgSdkViewModel.loginWithEmail(email, password, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithPhone(phone: String, verificationCode: String) {
        bringgSdkViewModel.loginWithPhone(phone, verificationCode, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithTokenAndSecret(region: String, token: String, secret: String) {
        bringgSdkViewModel.loginWithTokenAndSecret(region, token, secret, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithEmail(email: String, password: String, merchant: LoginMerchant) {
        bringgSdkViewModel.loginWithEmail(email, password, merchant, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithPhone(phone: String, verificationCode: String, merchant: LoginMerchant) {
        bringgSdkViewModel.loginWithPhone(phone, verificationCode, merchant, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun loginWithOIDCToken(region: String, token: String) {
        bringgSdkViewModel.loginWithOIDCToken(region, token, object : ResultCallback<DriverLoginResult> {
            override fun onResult(result: DriverLoginResult) {
                _loginResult.value = result
            }
        })
    }

    fun requestPhoneVerification(phone: String) {
        bringgSdkViewModel.requestPhoneVerification(phone)
    }

    fun requestResetPasswordLink(email: String) {
        bringgSdkViewModel.requestResetPasswordLink(email)
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
