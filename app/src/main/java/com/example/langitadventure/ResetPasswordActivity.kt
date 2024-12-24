package com.example.langitadventure

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val editTextEmailReset = findViewById<EditText>(R.id.editTextEmailReset)
        val buttonResetPassword = findViewById<Button>(R.id.buttonResetPassword)

        buttonResetPassword.setOnClickListener {
            val email = editTextEmailReset.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmailReset.error = "Email harus diisi"
                editTextEmailReset.requestFocus()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show()
                        finish() // Menutup activity ini
                    } else {
                        task.exception?.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }
}