package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.pwrpower.apk.ui.main.MainActivity
import androidx.core.content.edit
import com.pwrpower.apk.api.RegisterResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UserModel
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        setContentView(R.layout.activity_register_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameTextView: TextView = findViewById(R.id.nameSummaryEdit)
        val surnameTextView: TextView = findViewById(R.id.lastnameSummaryEdit)
        val emailTextView: TextView = findViewById(R.id.emailSummaryEdit)
        val phoneTextView: TextView = findViewById(R.id.phoneSummaryEdit)
        val licenseTextView: TextView = findViewById(R.id.licenseNumberSummaryEdit)
        val licenseExpTextView: TextView = findViewById(R.id.licenseExpSummaryEdit)

        val email: String = intent.getStringExtra("Email").toString()
        val password: String = intent.getStringExtra("Password").toString()
        val name: String = intent.getStringExtra("Name").toString()
        val surname: String = intent.getStringExtra("Surname").toString()
        val phone: String = intent.getStringExtra("Phone").toString()
        val licenseNumber: String = intent.getStringExtra("LicenseNumber").toString()
        val licenseExpDate: String = intent.getStringExtra("LicenseExpDate").toString()

        nameTextView.text = name
        surnameTextView.text = surname
        emailTextView.text = email
        phoneTextView.text = phone
        licenseTextView.text = licenseNumber
        licenseExpTextView.text = licenseExpDate

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {

            val userData = UserModel(email, password, name, surname, phone, licenseNumber, convertDate(licenseExpDate))

            RetrofitInstance.api.registerUser(userData).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, getString(R.string.register_good), Toast.LENGTH_LONG).show()

                        preferences.edit { putBoolean("isLoggedIn", true) }
                        val intent = Intent(this@RegisterSummaryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: getString(R.string.error_unknown)
                        Toast.makeText(applicationContext, getString(R.string.error_register), Toast.LENGTH_LONG).show()
                        Log.d("API_ERROR", "Error: ${response.code()}, Details: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d("API_ERROR", "Failure: ${t.message}")
                }
            })
        }
    }

    private fun convertDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(inputDate)

        return if (date != null) {
            outputFormat.format(date)
        } else {
            getString(R.string.date_invalid)
        }
    }
}
