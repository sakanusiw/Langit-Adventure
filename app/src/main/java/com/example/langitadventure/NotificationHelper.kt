package com.example.langitadventure.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.langitadventure.R

object NotificationHelper {

    const val CHANNEL_ID = "order_status_channel"
    private const val CHANNEL_NAME = "Order Status Notifications"
    private const val CHANNEL_DESC = "Notifications for order status updates"

    /**
     * Creates a notification channel for devices running Android Oreo (API 26) or higher.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            // Log untuk memastikan channel dibuat
            Log.d("NotificationHelper", "Notification channel created with ID: $CHANNEL_ID")
        }
    }

    /**
     * Sends a notification with the given title and message.
     */
    fun sendNotification(context: Context, title: String, message: String) {
        try {
            val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

            // Log untuk memastikan ID notifikasi dibuat dengan benar
            Log.d("NotificationHelper", "Generated notification ID: $notificationId")

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Replace with your app's notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                Log.d("NotificationHelper", "Sending notification with ID $notificationId")
                notify(notificationId, builder.build())
            }
            // Logging untuk menandai notifikasi berhasil dikirim
            Log.d("NotificationHelper", "Notification sent successfully with ID $notificationId")
        } catch (e: Exception) {
            // Logging error jika terjadi masalah
            Log.e("NotificationHelper", "Failed to send notification: ${e.message}", e)
        }
    }

    /**
     * Checks if notifications are enabled and logs the result.
     */
    fun checkNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

            if (!notificationsEnabled) {
                Log.w("NotificationHelper", "Notifications are disabled for this app. Please enable them in settings.")
            } else {
                Log.d("NotificationHelper", "Notifications are enabled.")
            }
        }
    }
}
