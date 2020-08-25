package com.bringg.android.example.driversdk.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import driver_sdk.account.LoginMerchant
import driver_sdk.driver.model.result.DriverLoginResult

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val usernameEditText = view.findViewById<EditText>(R.id.username)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.login)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                if (loginResult.success) {
                    showLoginSuccess()
                } else if (loginResult.userMerchantList.isNotEmpty()) {
                    val args = Bundle()
                    args.putParcelableArray("merchants", loginResult.userMerchantList.toTypedArray())
                    findNavController().navigate(R.id.login_merchant_selection, args)
                } else {
                    showLoginFailed(loginResult)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginWithEmail(usernameEditText, passwordEditText)
            }
            false
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LoginMerchant?>("merchant")?.observe(
            viewLifecycleOwner,
            Observer { merchant ->
                if (merchant != null) {
                    loginViewModel.loginWithEmail(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString(),
                        merchant
                    )
                }
            }
        )

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginWithEmail(usernameEditText, passwordEditText)
        }
    }

    private fun loginWithEmail(usernameEditText: EditText, passwordEditText: EditText) {
        loginViewModel.loginWithEmail(
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        )
    }

    private fun showLoginSuccess() {
        val welcome = getString(R.string.welcome)
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.task_list_fragment)
    }

    private fun showLoginFailed(result: DriverLoginResult) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, result.error!!.name(), Toast.LENGTH_LONG).show()
    }
}
