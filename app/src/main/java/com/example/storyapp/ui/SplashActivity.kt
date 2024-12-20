package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.databinding.ActivitySplashBinding
import com.example.storyapp.ui.list.ListActivity
import com.example.storyapp.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            val isLoggedIn = isUserLoggedIn()

            withContext(Dispatchers.Main) {
                handler = Handler(Looper.getMainLooper())
                handler?.postDelayed({
                    navigateToNextScreen(isLoggedIn)
                }, 1000)
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return !getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("access_token", null)
            .isNullOrEmpty()
    }

    private fun navigateToNextScreen(isLoggedIn: Boolean) {
        if (!isFinishing && !isDestroyed) {
            val intent = if (isLoggedIn) {
                Intent(this, ListActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onDestroy()
    }
}