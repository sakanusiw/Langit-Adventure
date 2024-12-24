package com.example.langitadventure

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import com.example.langitadventure.services.FirestoreService
import com.example.langitadventure.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            // Mulai FirestoreService jika pengguna login
            val intent = Intent(this, FirestoreService::class.java)
            startService(intent)
        } else {
            Log.w("MainActivity", "No user logged in. FirestoreService will not start.")
        }

        // Membuat Notification Channel
        NotificationHelper.createNotificationChannel(this)

        // Memeriksa izin notifikasi
        NotificationHelper.checkNotificationPermission(this)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
