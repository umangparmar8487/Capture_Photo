package com.example.capturephoto.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    /**
     * FusedLocationProviderClient is a class that provides access to the device's location services.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L) // 2000ms interval
        .setMinUpdateIntervalMillis(2000L) // Fastest interval is 2000ms
        .build()

    private var csvFile: File? = null
    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create CSV file in the Downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        csvFile = File(downloadsDir, "location_data_$timestamp.csv")

        /**
         * Create a notification channel for the foreground service.
         */
        createNotificationChannel()
        Log.d("LocationService", "CSV file created at: ${csvFile?.absolutePath}")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification("Waiting for location..."))
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    /**
     * Start requesting location updates.
     */
    private fun startLocationUpdates() {
        if (isServiceRunning) return
        isServiceRunning = true

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Stop requesting location updates.
     */
    private fun stopLocationUpdates() {
        if (!isServiceRunning) return
        isServiceRunning = false

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Callback for location updates.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                updateNotification(location)
                writeLocationToCsv(location)
            }
        }
    }

    /**
     * Function to write location data to a CSV file
     */
    private fun writeLocationToCsv(location: Location) {
        if (csvFile == null) {
            Log.e("LocationService", "CSV file is null")
            return
        }

        try {
            if (!csvFile!!.exists()) {
                // Add header row if the file is newly created
                csvFile!!.createNewFile()
                FileWriter(csvFile, true).use { writer ->
                    writer.append("Timestamp,Latitude,Longitude\n")
                }
            }

            FileWriter(csvFile, true).use { writer ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )
                val line = "$timestamp,${location.latitude},${location.longitude}\n"
                writer.append(line)
            }

        } catch (e: IOException) {
            Log.e("LocationService", "Error writing to CSV file", e)
        }
    }

    /**
     * Update the notification with the current location.
     */
    private fun updateNotification(location: Location) {
        val notification = createNotification("Lat: ${location.latitude}, Lon: ${location.longitude}")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    /**
     * Create a notification with the given message.
     */
    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Live Location")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    /**
     * Create a notification to show the location updates
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "location_channel",
            "Location Service",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

