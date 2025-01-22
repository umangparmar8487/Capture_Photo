package com.example.capturephoto.cameraactivity.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capturephoto.databinding.ActivityCameraBinding
import com.example.capturephoto.previewactivity.view.ImagePreviewActivity
import java.io.File
import android.util.Log
import android.util.Size
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.capturephoto.R
import com.example.capturephoto.cameraactivity.viewmodel.CameraViewModel
import com.example.capturephoto.location.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private val viewModel: CameraViewModel by viewModels()
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // File to save the captured image
        outputFile = File(getExternalFilesDir(null), "cropped_image.jpg")


        // Start the camera preview
        startCamera()

        // Observe the capture event
        viewModel.captureEvent.observe(this) { shouldCapture ->
            if (shouldCapture) {
                captureImage()
                viewModel.resetCaptureEvent()
            }
        }
    }

    /**
     * Sets up and starts the CameraX preview and image capture functionality.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview
            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Set up image capture
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1000, 1000))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()


            // Use the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind preview and image capture to the lifecycle
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Captures an image using CameraX and processes it.
     */
    private fun captureImage() {

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val croppedFilePath = viewModel.processAndSaveImage(this@CameraActivity,outputFile)
                        launch(Dispatchers.Main) {
                            navigateToPreview(croppedFilePath)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    /**
     * Navigates to the ImagePreviewActivity to display the captured image.
     */
    private fun navigateToPreview(filePath: String) {
        val intent = Intent(this, ImagePreviewActivity::class.java)
        intent.putExtra("image_path", filePath)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, LocationService::class.java))
    }
}
