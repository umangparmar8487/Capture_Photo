package com.example.capturephoto.homeactivity.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.capturephoto.R
import com.example.capturephoto.allinterface.PermissionListener
import com.example.capturephoto.cameraactivity.view.CameraActivity
import com.example.capturephoto.databinding.ActivityMainBinding
import com.example.capturephoto.homeactivity.viewmodel.MainViewModel
import com.example.capturephoto.utils.PermissionsUtil
import com.example.capturephoto.utils.PermissionsUtil.showPermissionDialog
import com.example.capturephoto.utils.PermissionsUtil.showSimpleDialog


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        locationPermissionRequest()

        binding.btnOpenCamera.setOnClickListener {
            cameraPermissionRequest()
        }
    }

    /**
     * Requests location-related permissions.
     */
    private fun locationPermissionRequest() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                POST_NOTIFICATIONS,
                ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                ACCESS_FINE_LOCATION
            )
        }

        PermissionsUtil.askForPermissions(
            this,
            permissions,
            object : PermissionListener {
                override fun permissionGranted(permission: String) {
                    println("$permission granted")
                }

                override fun permissionDenied(permission: String) {
                    showPermissionDialog(this@MainActivity,"",getString(R.string.permission_denied_message))
                }

                override fun permissionForeverDenied(permission: String) {
                    showSimpleDialog(this@MainActivity,"",getString(R.string.permission_denied_message))
                }
            }
        )

    }

    /**
     * Requests camera-related permissions.
     */
    private fun cameraPermissionRequest(){
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                CAMERA
            )
        } else {
            arrayOf(
                CAMERA,
                WRITE_EXTERNAL_STORAGE
            )
        }

        PermissionsUtil.askForPermissions(
            this,
            permissions,
            object : PermissionListener {
                override fun permissionGranted(permission: String) {
                    navigateToCameraActivity()
                }

                override fun permissionDenied(permission: String) {
                    showPermissionDialog(this@MainActivity,"",getString(R.string.permission_denied_message))
                }

                override fun permissionForeverDenied(permission: String) {
                    showSimpleDialog(this@MainActivity,"",getString(R.string.permission_denied_message))
                }
            }
        )
    }

    /**
     * Navigates to the CameraActivity.
     */
    private fun navigateToCameraActivity(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    /**
     * Handles the result of permission requests.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtil.handlePermissionsResult(requestCode, permissions, grantResults,this)
    }
}