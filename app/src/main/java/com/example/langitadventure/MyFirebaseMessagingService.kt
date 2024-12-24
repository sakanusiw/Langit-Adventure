package com.example.langitadventure

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.langitadventure.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Cek apakah pesan mengandung notifikasi
        remoteMessage.notification?.let {
            // Menampilkan notifikasi menggunakan NotificationHelper
            NotificationHelper.sendNotification(
                applicationContext,
                it.title ?: "Notifikasi Baru",
                it.body ?: "Ada pembaruan terbaru!"
            )
        }

        // Cek apakah pesan mengandung data
        remoteMessage.data.isNotEmpty().let {
            // Jika ada data, log data untuk debugging
            Log.d(TAG, "Data payload: ${remoteMessage.data}")
            // Anda dapat melakukan logika tambahan di sini (misalnya, update UI atau database)
        }
    }

    // Menangani pesan yang diterima saat aplikasi pertama kali dimulai
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Menangani token baru yang diberikan oleh FCM
        Log.d(TAG, "Token Firebase baru: $token")
        // Anda dapat mengirim token ini ke server untuk tujuan pengiriman pesan di masa depan
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}
