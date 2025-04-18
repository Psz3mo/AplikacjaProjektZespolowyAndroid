package com.pwrpower.apk.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //private const val BASE_URL = "http://34.163.121.92:5000/api/" // Server google cloud
    //private const val BASE_URL = "http://192.168.100.20:5000/api/" // Localhost wroclaw
    private const val BASE_URL = "http://192.168.1.57:5000/api/" // Localhost


    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}