package com.example.storyapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.api.LoginRequest
import com.example.storyapp.ui.list.ListActivity
import com.example.storyapp.ui.register.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

            setupActions()
    }

    private fun setupActions() {
        binding.registerTV.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            if (isInputValid()) {
                val email = binding.etLoginEmail.text.toString()
                val password = binding.etLoginPassword.text.toString()
                showLoading()
                loginUser(email, password)
            }
        }
    }

    private fun isInputValid(): Boolean {
        val isEmailValid = binding.etLoginEmail.isValid()
        val isPasswordValid = binding.etLoginPassword.isValid()

        return isEmailValid && isPasswordValid
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        val apiService = ApiConfig.getApiService()

        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideLoading()
                if (response.isSuccessful) {
                    response.body()?.loginResult?.token?.let {
                        saveAccessToken(it)
                        navigateToList()
                    }
                } else {
                    showNotification("Incorrect email or password")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                hideLoading()
                showNotification("Login failed: ${t.message}")
            }
        })
    }

    private fun saveAccessToken(token: String) {
        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit().apply {
            putString("access_token", token)
            apply()
        }
    }


    private fun navigateToList() {
        startActivity(Intent(this, ListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showLoading() {
        binding.loadingCard.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideLoading() {
        binding.loadingCard.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showNotification(message: String) {
        binding.tvLoginNotification.text = message
        binding.loginNotificationLayout.visibility = View.VISIBLE

        binding.loginNotificationLayout.postDelayed({
            binding.loginNotificationLayout.visibility = View.GONE
        }, 3000)
    }
}
