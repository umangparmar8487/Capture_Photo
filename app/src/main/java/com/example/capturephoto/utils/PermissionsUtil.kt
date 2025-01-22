package com.example.capturephoto.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capturephoto.R
import com.example.capturephoto.allinterface.PermissionListener

object PermissionsUtil {

    const val KEY_PERMISSION_REQUEST = 100

    private var permissionListener: PermissionListener? = null

    /**
     * Request permissions with a listener callback.
     */
    fun askForPermissions(
        activity: Activity,
        permissions: Array<String>,
        listener: PermissionListener
    ) {
        permissionListener = listener
        internalRequestPermission(activity, permissions)
    }

    /**
     * Internal function to check and request ungranted permissions.
     */
    private fun internalRequestPermission(activity: Activity, permissions: Array<String>) {
        val permissionsNotGranted = mutableListOf<String>()

        for (permission in permissions) {
            if (!isPermissionGranted(activity, permission)) {
                permissionsNotGranted.add(permission)
            } else {
                permissionListener?.permissionGranted(permission)
            }
        }

        if (permissionsNotGranted.isNotEmpty()) {
            val arrayPermissionNotGranted = permissionsNotGranted.toTypedArray()
            ActivityCompat.requestPermissions(
                activity,
                arrayPermissionNotGranted,
                KEY_PERMISSION_REQUEST
            )
        }
    }

    /**
     * Helper function to check if a permission is granted.
     */
    private fun isPermissionGranted(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handle the result of permission requests. Call this from the Activity's onRequestPermissionsResult().
     */
    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == KEY_PERMISSION_REQUEST) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val granted = grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED

                if (granted) {
                    permissionListener?.permissionGranted(permission)
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    permissionListener?.permissionForeverDenied(permission)
                } else {
                    permissionListener?.permissionDenied(permission)
                }
            }
        }
    }

    /**
     * Show a dialog to guide users to app settings for enabling permissions.
     */
    fun showPermissionDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.setting)) { _, _ ->
                openAppSettings(context)
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    /**
     * Show a simple informational dialog.
     */
    fun showSimpleDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok), null)
            .show()
    }

    /**
     * Open the app's settings page for the user to manually enable permissions.
     */
    private fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}
