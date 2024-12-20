package com.example.storyapp.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.UploadResponse
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.ui.list.ListActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var currentImageUri: Uri? = null
    private var lastValidImageUri: Uri? = null
    private var currentPhotoPath: String = ""
    private var isFromCamera = false
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val MAX_FILE_SIZE = 1024 * 1024 // 1MB in bytes

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            lastValidImageUri = currentImageUri
            isFromCamera = true
        } else {
            currentImageUri = lastValidImageUri
        }
        showImage()
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            currentImageUri = uri
            lastValidImageUri = uri
            isFromCamera = false
            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions() {
        binding.cardUploadImage.setOnClickListener {
            showImageUploadOptions()
        }

        binding.uploadButton.setOnClickListener {
            uploadStory()
        }
    }

    private fun showImageUploadOptions() {
        AlertDialog.Builder(this)
            .setTitle("Upload Photo")
            .setPositiveButton("Take Photo") { _, _ ->
                checkPermissionsAndStartCamera()
            }
            .setNegativeButton("Choose from Gallery") { _, _ ->
                openGallery()
            }
            .show()
    }

    private fun checkPermissionsAndStartCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val timeStamp = SimpleDateFormat(
            "dd-MMM-yyyy",
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile(
            "STORY_${timeStamp}_",
            ".jpg",
            storageDir
        )

        currentPhotoPath = photoFile.absolutePath
        currentImageUri = FileProvider.getUriForFile(
            this,
            "com.example.storyapp.provider",
            photoFile
        )

        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun openGallery() {
        launcherIntentGallery.launch("image/*")
    }

    private fun showImage() {
        val uriToShow = currentImageUri ?: lastValidImageUri
        uriToShow?.let {
            binding.uploadImageView.apply {
                setImageURI(it)
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
            }
        }
    }

    private fun checkFileSize(file: File): Boolean {
        return file.length() <= MAX_FILE_SIZE
    }

    private fun uploadStory() {
        val uriToUpload = currentImageUri ?: lastValidImageUri
        if (uriToUpload == null) {
            showToast("Please select an image")
            return
        }

        val description = binding.descEditText.text.toString()
        if (description.isEmpty()) {
            showToast("Please add a description")
            return
        }

        showLoading(true)


        val imageFile = if (isFromCamera && currentPhotoPath.isNotEmpty()) {
            File(currentPhotoPath)
        } else {
            val timeStamp = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                .format(System.currentTimeMillis())
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val destinationFile = File(
                storageDir,
                "STORY_${timeStamp}_${System.currentTimeMillis()}.jpg"
            )

            contentResolver.openInputStream(uriToUpload)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile
        }


        if (!checkFileSize(imageFile)) {
            showLoading(false)
            showToast("File size exceeds 1MB limit")
            return
        }

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("access_token", "") ?: ""

        val apiService = ApiConfig.getApiService()
        apiService.addStory(
            "Bearer $token",
            imageMultipart,
            descriptionRequestBody,
            null,  // lat
            null   // lon
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    showToast("Story uploaded successfully!")
                    startActivity(Intent(this@AddActivity, ListActivity::class.java))
                    setResult(RESULT_OK)
                    finish()
                } else {
                    showToast("Failed to upload story")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                showLoading(false)
                showToast("Error: ${t.message}")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                showToast("Camera permission required")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}