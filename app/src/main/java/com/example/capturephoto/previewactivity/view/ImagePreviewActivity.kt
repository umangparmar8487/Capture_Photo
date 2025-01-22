package com.example.capturephoto.previewactivity.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capturephoto.databinding.ActivityImagePreviewBinding
import com.example.capturephoto.previewactivity.viewmodel.PreviewViewModel

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePreviewBinding
    private val viewModel: PreviewViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the captured image
        val filePath = getExternalFilesDir(null)?.absolutePath + "/cropped_image.jpg"
        viewModel.loadCapturedImage(filePath)

        // Observe LiveData for the captured image
        viewModel.capturedImage.observe(this) { bitmap ->
            updateImageView(bitmap)
        }
    }

    /**
     * Updates the ImageView with the captured bitmap.
     */
    private fun updateImageView(bitmap: Bitmap?) {
        bitmap?.let {
            binding.imageView.setImageBitmap(it)
        }
    }
}
