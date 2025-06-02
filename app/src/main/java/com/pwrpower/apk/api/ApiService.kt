package com.pwrpower.apk.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users/register")
    fun registerUser(@Body user: UserModel): Call<RegisterResponse>

    @POST("users/check-email")
    fun checkEmail(@Body email: EmailModel): Call<CheckEmailResponse>

    @POST("users/check-phone")
    fun checkPhone(@Body phone: PhoneModel): Call<CheckPhoneResponse>

    @POST("users/login")
    fun loginUser(@Body login: LoginModel): Call<LoginResponse>

    @GET("users/{id}")
    fun getUserData(@Path("id") userId: Int): Call<UserDataResponse>

    @PUT("users/{id}")
    fun updateUserData(@Path("id") userId: Int, @Body user: UserDataModel): Call<UpdateUserResponse>

    @POST("users/verify-password")
    fun verifyPassword(@Body password: PasswordModel): Call<VerifyPasswordResponse>

    @PUT("users/{id}/change-password")
    fun changePassword(@Path("id") userId: Int, @Body newPassword: NewPasswordModel): Call<ChangePasswordResponse>

    @PUT("users/{id}/change-email")
    fun changeEmail(@Path("id") userId: Int, @Body newEmail: NewEmailModel): Call<ChangeEmailResponse>

    @GET("users/{id}/balance")
    fun getUserBalance(@Path("id") userId: Int): Call<BalanceResponse>

    @POST("users/{id}/add-payment")
    fun addPayment(@Path("id") userId: Int, @Body payment: PaymentModel): Call<AddPaymentResponse>

    @GET("users/{id}/transactions")
    fun getUserTransactions(@Path("id") userId: Int): Call<TransactionsResponse>

    @GET("cars")
    fun getCars(): Call<CarsResponse>

    @GET("cars/{id}")
    fun getCarDetails(@Path("id") carId: Int): Call<CarInfoResponse>

    @GET("cars/{id}/reviews")
    fun getReviews(@Path("id") carId: Int): Call<GetReviewsResponse>

    @POST("cars/{id}/location")
    fun sendCarLocation(@Path("id") carId: Int, @Body location: LocationModel): Call<SendLocationResponse>

    @PATCH("cars/{id}/status")
    fun updateCarStatus(@Path("id") carId: Int, @Body status: CarStatusModel): Call<UpdateCarStatusResponse>

    @POST("users/{id}/rent")
    fun rentCar(@Path("id") userId: Int, @Body rentDetails: RentDetailsModel): Call<RentResponse>
}