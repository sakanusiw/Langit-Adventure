package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.langitadventure.HomeActivity
import com.example.langitadventure.OrderActivity
import com.example.langitadventure.R
import com.example.langitadventure.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val buttonMasuk = findViewById<Button>(R.id.buttonMasuk)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        buttonMasuk.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Validasi kolom kosong
            if (email.isEmpty()) {
                editTextEmail.error = "Email harus diisi"
                editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password harus diisi"
                editTextPassword.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user?.isEmailVerified == true) {
                            // Email is verified, proceed to main activity
                            val intent = Intent(this, OrderActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Email is not verified, show message
                            Toast.makeText(this, "Please verify your email address.", Toast.LENGTH_LONG).show()
                            auth.signOut() // Sign out the user
                        }
                    } else {
                        // Login failed, show error message
                        task.exception?.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

        // Declaring and initializing the TextView from layout
        val mTextView = findViewById<TextView>(R.id.textViewLupaSandi)

        // Declaring a string
        val mString = "Lupa Sandi"

        // Creating a Spannable String from the above string
        val mSpannableString = SpannableString(mString)

        // Setting underline style from position 0 till length of the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable string in TextView
        mTextView.text = mSpannableString

        val buttonClick1 = findViewById<TextView>(R.id.textViewDaftar)
        buttonClick1.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonClick2 = findViewById<ImageButton>(R.id.imageButtonLewati)
        buttonClick2.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}