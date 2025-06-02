package com.pwrpower.apk.api

import android.provider.ContactsContract.CommonDataKinds.Photo

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

data class PaymentModel(
    val amount: Double,
    val description: String
)

data class Transaction(
    val amount: Double,
    val type: String,
    val created_at: String,
    val description: String,
    val status: Int
)

data class Cars(
    val id: Int,
    val name: String,
    val price: Double,
    val photo: String,
    val rating: Double
)

data class Car(
    val brand: String,
    val model: String,
    val price: Double,
    val photo: String,
    val rating: Double,
    val color: String,
    val year: Int,
    val engineCapacity: Double,
    val fuelType: String,
    val fuelConsumption: Double,
    val seats: Int,
    val bluetoothName: String,
    val serviceUUID: String,
    val controlUUID: String,
    val gpsUUID: String
)

data class Review(
    val rating: Double,
    val review: String,
    val date: String,
    val name: String
)

data class LocationModel(
    val latitude: Double,
    val longitude: Double
)

data class CarStatusModel(
    val status: String
)

data class RentDetailsModel(
    val carId: Int,
    val startDate: String,
    val endDate: String,
    val rentCost: Double,
    val paid: Int
)