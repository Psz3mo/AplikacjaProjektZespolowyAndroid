package com.pwrpower.apk.api

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
)

data class CheckEmailResponse(
    val success: Boolean,
    val exists: Boolean,
    val message: String,
    val errors: List<String>? = null,
)

data class CheckPhoneResponse(
    val success: Boolean,
    val exists: Boolean,
    val message: String,
    val errors: List<String>? = null,
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null,
)