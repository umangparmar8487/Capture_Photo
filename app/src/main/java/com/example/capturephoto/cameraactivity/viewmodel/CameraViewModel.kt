package com.example.capturephoto.cameraactivity.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream

class CameraViewModel : ViewModel() {

    private val _captureEvent = MutableLiveData<Boolean>() // LiveData for the capture event
    val captureEvent: LiveData<Boolean> get() = _captureEvent

    /**
     * Triggered when the capture button is clicked.
     */
    fun captureImage() {
        _captureEvent.value = true
    }

    /**
     * Processes the captured image, crops it, and saves the result.
     * @param file The original image file.
     * @return The path of the cropped image.
     */
    fun processAndSaveImage(context: Context, file: File): String {
        if (!file.exists()) return file.absolutePath

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val croppedBitmap = cropBitmap(bitmap)
        saveBitmapToFile(croppedBitmap, file)
        saveBitmapToGallery(context, croppedBitmap)
        return file.absolutePath
    }
    /**
     * Crops the given bitmap to a central viewport rectangle.
     */
    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val rect = Rect(width / 4, height / 4, (3 * width) / 4, (3 * height) / 4)
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }

    /**
     * Saves the given bitmap to the specified file.
     */
    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    /**
     * Saves the given bitmap to the Gallery using MediaStore.
     * @return The URI of the saved image as a string, or null if the save failed.
     */
     fun saveBitmapToGallery(context: Context, bitmap: Bitmap): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCapturedImages")
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            }
            return uri.toString()
        }
        return null
    }

    /**
     * Resets the capture event after it is handled.
     */
    fun resetCaptureEvent() {
        _captureEvent.value = false
    }
}
