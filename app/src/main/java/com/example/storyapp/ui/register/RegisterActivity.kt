package com.example.storyapp.ui.register


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.RegisterResponse
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.api.RegisterRequest
import com.example.storyapp.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions() {
        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            if (isInputValid()) {
                val name = binding.usernameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                showLoading()
                registerUser(name, email, password)
            }
        }
    }

    private fun isInputValid(): Boolean {
        val isUsernameValid = binding.usernameEditText.isValid()
        val isEmailValid = binding.emailEditText.isValid()
        val isPasswordValid = binding.passwordEditText.isValid()

        return isUsernameValid && isEmailValid && isPasswordValid
    }

    private fun registerUser(name: String, email: String, password: String) {
        val registerRequest = RegisterRequest(name, email, password)
        val apiService = ApiConfig.getApiService()

        apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    showNotification("Registration successful! Please login.", isSuccess = true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }, 1000)
                } else {
                    showNotification("Registration failed. Please try again.")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                hideLoading()
                showNotification("Registration failed: ${t.message}")
            }
        })
    }

    private fun showLoading() {
        binding.loadingCard.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoading() {
        binding.loadingCard.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showNotification(message: String, isSuccess: Boolean = false) {
        binding.apply {
            tvNotification.text = message

            if (isSuccess) {
                notificationLayout.setBackgroundColor(ContextCompat.getColor(this@RegisterActivity, R.color.success_bg))
                tvNotification.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.success_text))
            } else {
                notificationLayout.setBackgroundColor(Color.parseColor("#FFD6D6"))
                tvNotification.setTextColor(Color.parseColor("#FF5C5C"))
            }

            notificationLayout.visibility = View.VISIBLE

            notificationLayout.postDelayed({
                notificationLayout.visibility = View.GONE
            }, 3000)
        }
    }
}