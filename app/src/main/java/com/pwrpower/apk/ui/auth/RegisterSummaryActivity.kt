package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.pwrpower.apk.ui.main.MainActivity
import androidx.core.content.edit

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

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            preferences.edit { putBoolean("isLoggedIn", true) }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}