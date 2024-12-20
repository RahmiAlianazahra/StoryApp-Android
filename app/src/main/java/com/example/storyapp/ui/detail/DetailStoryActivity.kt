package com.example.storyapp.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.Story
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.api.DetailStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId != null) {
            fetchStoryDetail(storyId)
        }

        binding.apply {
            ivDetailPhoto.alpha = 0f
            tvDetailName.alpha = 0f
            tvDetailDescription.alpha = 0f
            tvDetailDate.alpha = 0f

            ivDetailPhoto.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

            tvDetailName.animate()
                .alpha(1f)
                .setStartDelay(100)
                .setDuration(300)
                .start()

            tvDetailDescription.animate()
                .alpha(1f)
                .setStartDelay(200)
                .setDuration(300)
                .start()

            tvDetailDate.animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(300)
                .start()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.apply {
            ivDetailPhoto.animate()
                .alpha(0f)
                .setDuration(200)
                .start()

            tvDetailName.animate()
                .alpha(0f)
                .setDuration(200)
                .start()

            tvDetailDescription.animate()
                .alpha(0f)
                .setDuration(200)
                .start()

            tvDetailDate.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    finish()
                    overridePendingTransition(0, R.anim.slide_out_right)
                }
                .start()
        }
    }

    private fun fetchStoryDetail(storyId: String) {
        showLoading(true)
        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("access_token", "") ?: ""

        val apiService = ApiConfig.getApiService()
        apiService.getStoryDetail("Bearer $token", storyId)
            .enqueue(object : Callback<DetailStoryResponse> {
                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.story?.let { story ->
                            displayStoryDetail(story)
                        }
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    showLoading(false)
                }
            })
    }

    private fun displayStoryDetail(story: Story) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            val date = parser.parse(story.createdAt)
            tvDetailDate.text = formatter.format(date)

            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(ivDetailPhoto)

            if (story.lat != null && story.lon != null) {
                tvLocation.visibility = View.VISIBLE
                tvLocation.text = "Location: ${story.lat}, ${story.lon}"
            } else {
                tvLocation.visibility = View.GONE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}