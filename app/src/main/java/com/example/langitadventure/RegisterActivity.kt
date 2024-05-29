package com.example.langitadventure

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
//import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                val request = RegisterRequest(name, email, password, confirmPassword)
                RetrofitClient.instance.register(request)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, "Register successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@RegisterActivity, "Register failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
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