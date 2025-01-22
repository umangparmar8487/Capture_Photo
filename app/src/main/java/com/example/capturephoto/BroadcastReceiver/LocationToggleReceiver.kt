package com.example.capturephoto.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.capturephoto.location.LocationService

class LocationToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.capturephoto.ACTION_TOGGLE_LOCATION") {
            val isServiceRunning = intent.getBooleanExtra("is_running", false)

            if (isServiceRunning) {
                /**
                 * Stop Location Service
                 */
                val serviceIntent = Intent(context, LocationService::class.java)
                context.stopService(serviceIntent)
                Toast.makeText(context, "Location Service Stopped", Toast.LENGTH_SHORT).show()
            } else {
                /**
                 * start Location Service
                 */
                val serviceIntent = Intent(context, LocationService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
                Toast.makeText(context, "Location Service Started", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

