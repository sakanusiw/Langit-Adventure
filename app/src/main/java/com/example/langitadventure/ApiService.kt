package com.example.langitadventure

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header

data class UpdateProfileRequest(val name: String, val email: String, val password: String, val password_confirmation: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val password_confirmation: String)
data class LoginRequest(val email: String, val password: String)

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<ResponseBody>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<ResponseBody>

    @POST("user/update")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest): Call<ResponseBody>

}