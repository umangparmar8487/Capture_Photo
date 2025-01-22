package com.example.capturephoto.previewactivity.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class PreviewViewModel : ViewModel() {

    private val _capturedImage = MutableLiveData<Bitmap>()
    val capturedImage: LiveData<Bitmap>
        get() = _capturedImage

    /**
     * Loads the captured image from the local file.
     */
    fun loadCapturedImage(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            _capturedImage.postValue(bitmap)
        }
    }
}
