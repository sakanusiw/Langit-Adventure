package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaymentSentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_sent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonClick = findViewById<Button>(R.id.buttonPaymentSentLanjut)
        buttonClick.setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(com.google.android.material.R.id.container, OrderFragment()).commit()
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }
}