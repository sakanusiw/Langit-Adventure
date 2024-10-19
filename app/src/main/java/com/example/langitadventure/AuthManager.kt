package com.example.langitadventure

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import android.widget.EditText
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.Exception

class AuthManager(private val context: Context) {

    companion object {
        const val BASE_URL = "https://guiding-uniquely-piglet.ngrok-free.app/api/"
        private const val TAG = "AuthManager"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
        Log.d(TAG, "Token saved: $token")
    }

    fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        Log.d(TAG, "Token retrieved: $token")
        return token
    }

    fun fetchUserProfile() {
        val token = getToken()

        if (token != null) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(BASE_URL + "user")
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error fetching user profile: ${e.message}", e)
                    runOnUiThread {
                        Toast.makeText(context, "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.e(TAG, "Failed to fetch user profile: HTTP ${response.code()}")
                            runOnUiThread {
                                Toast.makeText(context, "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                            }
                            return
                        }

                        val responseBody = response.body()?.string()
                        if (responseBody != null) {
                            try {
                                val jsonObject = JSONObject(responseBody)
                                val userName = jsonObject.getString("name")
                                val userEmail = jsonObject.getString("email")
                                // Update UI on main thread
                                runOnUiThread {
                                    (context as? Activity)?.findViewById<TextView>(R.id.textViewUserNameProfile)?.text = userName
                                    (context as? Activity)?.findViewById<TextView>(R.id.textViewUserEmailProfile)?.text = userEmail
                                    (context as? Activity)?.findViewById<EditText>(R.id.editTextNamaLengkap)?.setText(userName)
                                    (context as? Activity)?.findViewById<EditText>(R.id.editTextEmail)?.setText(userEmail)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing user profile: ${e.message}", e)
                                runOnUiThread {
                                    Toast.makeText(context, "Failed to parse user profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e(TAG, "Response body is null")
                            runOnUiThread {
                                Toast.makeText(context, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })
        } else {
            Log.e(TAG, "Token is null")
        }
    }

    fun logout() {
        val token = getToken()

        if (token != null) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(BASE_URL + "logout")
                .addHeader("Authorization", "Bearer $token")
                .post(RequestBody.create(null, ""))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error during logout: ${e.message}", e)
                    runOnUiThread {
                        Toast.makeText(context, "Failed to logout", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val editor = sharedPreferences.edit()
                        editor.remove("auth_token")
                        editor.apply()
                        runOnUiThread {
                            Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                            // Example: Redirect to login screen
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            (context as? Activity)?.finish()
                        }
                    } else {
                        Log.e(TAG, "Failed to logout: HTTP ${response.code()}")
                        runOnUiThread {
                            Toast.makeText(context, "Failed to logout", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        } else {
            Log.e(TAG, "Token is null")
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        if (context is Activity) {
            context.runOnUiThread {
                action()
            }
        }
    }
}
