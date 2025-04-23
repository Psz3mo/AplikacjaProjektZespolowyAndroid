package com.pwrpower.apk.api

data class UserModel(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val licenseNumber: String,
    val licenseExpiration: String
)

data class EmailModel(
    val email: String
)

data class PhoneModel(
    val phone: String
)

data class LoginModel(
    val email: String,
    val password: String
)

data class UserDataModel(
    val name: String,
    val surname: String,
    val phone: String,
    val licenseNumber: String,
    val licenseExpiration: String
)

data class PasswordModel(
    val accountId: Int,
    val password: String
)

data class NewPasswordModel(
    val password: String
)

data class NewEmailModel(
    val email: String
)