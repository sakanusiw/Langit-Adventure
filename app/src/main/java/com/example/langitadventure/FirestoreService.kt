package com.example.langitadventure.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.langitadventure.R
import com.example.langitadventure.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService : Service() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate() {
        super.onCreate()

        // Dapatkan UID pengguna yang sedang login
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            // Listening to order status changes untuk pengguna yang sedang login
            listenToOrderUpdates(currentUserUid)
        } else {
            Log.w("FirestoreService", "No user logged in. Stopping service.")
            stopSelf() // Hentikan service jika tidak ada pengguna yang login
        }

        startForegroundService()
        if (currentUserUid != null) {
            listenToOrderUpdates(currentUserUid)
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Listening for Order Updates")
            .setContentText("Kami sedang memantau status pesanan Anda.")
            .setSmallIcon(R.drawable.notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun listenToOrderUpdates(userUid: String) {
        firestore.collection("users")
            .document(userUid)
            .collection("orders")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("FirestoreService", "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (doc in snapshots!!.documentChanges) {
                    val orderData = doc.document.data
                    val status = orderData["status"] as? String
                    val itemName = orderData["itemName"] as? String ?: "Pesanan"

                    if (status != null) {
                        NotificationHelper.sendNotification(
                            this,
                            "Status Pesanan",
                            "Status untuk $itemName telah diperbarui: $status"
                        )
                    }
                }
            }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
