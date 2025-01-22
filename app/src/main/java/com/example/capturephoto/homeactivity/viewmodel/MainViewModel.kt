package com.example.capturephoto.homeactivity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _openCamera = MutableLiveData(false)
    val openCamera: LiveData<Boolean> get() = _openCamera

    /**
     * Called when the "Open Camera" button is clicked.
     */
    fun onOpenCameraClicked() {
        _openCamera.value = true
    }

    /**
     * Reset the event after handling.
     */
    fun resetOpenCameraEvent() {
        _openCamera.value = false
    }
}

