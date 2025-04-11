package com.pwrpower.apk.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("users/register")
    fun registerUser(@Body user: UserModel): Call<RegisterResponse>

    @POST("users/checkEmail")
    fun checkEmail(@Body email: EmailModel): Call<CheckEmailResponse>

    @POST("users/checkPhone")
    fun checkPhone(@Body phone: PhoneModel): Call<CheckPhoneResponse>

    @POST("users/login")
    fun loginUser(@Body login: LoginModel): Call<LoginResponse>
}