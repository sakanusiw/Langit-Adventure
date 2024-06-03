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
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)

        val buttonMasuk = findViewById<Button>(R.id.buttonMasuk)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        buttonMasuk.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            val request = LoginRequest(email, password)
            RetrofitClient.instance.login(request)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            responseBody?.let {
                                try {
                                    val jsonObject = JSONObject(it)
                                    val token = jsonObject.getString("token")
                                    authManager.saveToken(token)

                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } catch (e: Exception) {
                                    Toast.makeText(this@LoginActivity, "Failed to parse token", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
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
