package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var editTextNamaLengkap: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextKonfirmasiPassword: EditText
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var buttonSimpan: Button
    private lateinit var imageButtonBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        authManager = AuthManager(this)

        editTextNamaLengkap = findViewById(R.id.editTextNamaLengkap)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextKonfirmasiPassword = findViewById(R.id.editTextKonfirmasiPassword)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        buttonSimpan = findViewById(R.id.buttonSimpan)
        imageButtonBack = findViewById(R.id.imageButtonBack)

        // Fetch current user data to populate fields
        authManager.fetchUserProfile()

        buttonSimpan.setOnClickListener {
            updateUserProfile()
        }

        imageButtonBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUserProfile() {
        val name = editTextNamaLengkap.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextKonfirmasiPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val requestData = UpdateProfileRequest(name, email, password, confirmPassword)
        val token = authManager.getToken()
        Log.d("ProfileEditActivity", "Token: $token")

        if (token != null) {
            val gson = Gson()
            val jsonRequestData = gson.toJson(requestData)
            val mediaType = MediaType.parse("application/json; charset=utf-8")
            val requestBody = RequestBody.create(mediaType, jsonRequestData)

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(AuthManager.BASE_URL + "user/update")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Log.e("ProfileEditActivity", "Request Failed", e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        Log.d("ProfileEditActivity", "Request Successful")
                        val intent = Intent(this@ProfileEditActivity, ProfileActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@ProfileEditActivity, "Profile Edit Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileEditActivity, "Profile Edit Failed", Toast.LENGTH_SHORT).show()
                        Log.e("ProfileEditActivity", "Request Failed: ${response.body()}")
                    }
                }
            })
        } else {
            Log.e("ProfileEditActivity", "Token is null")
        }
    }
}
