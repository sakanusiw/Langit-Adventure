package com.example.langitadventure

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
//    private const val BASE_URL = "http://192.168.1.10:8000/api/"
    private const val BASE_URL = "https://guiding-uniquely-piglet.ngrok-free.app/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}