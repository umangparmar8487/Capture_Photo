package com.example.capturephoto.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.capturephoto.BroadcastReceiver.LocationToggleReceiver
import com.example.capturephoto.R
import com.example.capturephoto.cameraactivity.view.CameraActivity

class MyWidgetProvider : AppWidgetProvider() {

    @SuppressLint("RemoteViewLayout")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            /**
             * Intent for "Click Image" button
             */
            val cameraIntent = Intent(context, CameraActivity::class.java)
            val cameraPendingIntent = PendingIntent.getActivity(
                context,
                0,
                cameraIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetButton, cameraPendingIntent)

            /**
             * Intent for "Location Service" toggle
             */
            val toggleIntent = Intent(context, LocationToggleReceiver::class.java)
            toggleIntent.action = "com.example.capturephoto.ACTION_TOGGLE_LOCATION"
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetSwitch, togglePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

