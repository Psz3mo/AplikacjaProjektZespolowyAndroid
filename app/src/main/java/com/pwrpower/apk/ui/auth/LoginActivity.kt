package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.LoginModel
import com.pwrpower.apk.ui.main.MainActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput: TextInputEditText = findViewById(R.id.emailEditText)
        val passwordInput: TextInputEditText = findViewById(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {

            if(emailInput.text.isNullOrEmpty() || passwordInput.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.login_error_1), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitInstance.api.loginUser(LoginModel(emailInput.text.toString(), passwordInput.text.toString()))
                .enqueue(object : retrofit2.Callback<com.pwrpower.apk.api.LoginResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<com.pwrpower.apk.api.LoginResponse>,
                        response: retrofit2.Response<com.pwrpower.apk.api.LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                if (loginResponse.success) {
                                    Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                    preferences.edit { putBoolean("isLoggedIn", true) }
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.login_error_2), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<com.pwrpower.apk.api.LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_error_3), Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}