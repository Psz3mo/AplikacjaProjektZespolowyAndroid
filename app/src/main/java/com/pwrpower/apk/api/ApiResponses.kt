package com.pwrpower.apk.api

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val userId: Int? = null
)

data class CheckEmailResponse(
    val success: Boolean,
    val exists: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class CheckPhoneResponse(
    val success: Boolean,
    val exists: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val userId: Int? = null
)

data class UserDataResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val user: UserModel? = null
)

data class UpdateUserResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class VerifyPasswordResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class ChangeEmailResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)

data class BalanceResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val balance: Double? = null
)

data class AddPaymentResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val status: Int = 0
)

data class TransactionsResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val transactions: List<Transaction>? = null
)

data class CarsResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val cars: List<Cars>? = null
)

data class CarInfoResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val car: Car? = null
)

data class GetReviewsResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
    val reviews: List<Review>? = null
)

data class SendLocationResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)