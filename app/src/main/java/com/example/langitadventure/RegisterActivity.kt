package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val buttonDaftar = findViewById<Button>(R.id.buttonDaftar)
        val editTextNamaLengkap = findViewById<EditText>(R.id.editTextNamaLengkap)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextKonfirmasiPassword = findViewById<EditText>(R.id.editTextKonfirmasiPassword)

        buttonDaftar.setOnClickListener {
            val name = editTextNamaLengkap.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextKonfirmasiPassword.text.toString().trim()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    val userData = hashMapOf(
                                        "username" to name,
                                        "email" to email,
                                        "uid" to user.uid
                                    )
                                    db.collection("users").document(user.uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show()
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle failure
                                            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    // Failed to send verification email
                                    Toast.makeText(this, "Failed to send verification email: ${verifyTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            // Registration failed
                            task.exception?.message?.let {
                                // Show error message
                                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                // Passwords do not match
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        }

        val buttonClick = findViewById<ImageButton>(R.id.imageButtonKembali)
        buttonClick.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        val buttonClick1 = findViewById<Button>(R.id.buttonDaftar)
//        buttonClick1.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
    }
}