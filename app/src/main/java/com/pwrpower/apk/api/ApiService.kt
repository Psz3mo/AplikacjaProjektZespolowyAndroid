package com.pwrpower.apk.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users/register")
    fun registerUser(@Body user: UserModel): Call<RegisterResponse>

    @POST("users/checkEmail")
    fun checkEmail(@Body email: EmailModel): Call<CheckEmailResponse>

    @POST("users/checkPhone")
    fun checkPhone(@Body phone: PhoneModel): Call<CheckPhoneResponse>

    @POST("users/login")
    fun loginUser(@Body login: LoginModel): Call<LoginResponse>

    @GET("users/user/{id}")
    fun getUserData(@Path("id") userId: Int): Call<UserDataResponse>

    @PUT("users/user/{id}")
    fun updateUserData(@Path("id") userId: Int, @Body user: UserDataModel): Call<UpdateUserResponse>

    @POST("users/verifyPassword")
    fun verifyPassword(@Body password: PasswordModel): Call<VerifyPasswordResponse>

    @PUT("users/changePassword/{id}")
    fun changePassword(@Path("id") userId: Int, @Body newPassword: NewPasswordModel): Call<ChangePasswordResponse>

    @PUT("users/changeEmail/{id}")
    fun changeEmail(@Path("id") userId: Int, @Body newEmail: NewEmailModel): Call<ChangeEmailResponse>
}