package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pwrpower.apk.R
import com.pwrpower.apk.ui.main.MainActivity
import com.pwrpower.apk.ui.others.SettingsFormActivity

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isLoggedIn: Boolean = preferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) { // Check if user is logged in
            setContentView(R.layout.activity_auth)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val registerButton: Button = findViewById(R.id.signUpButton)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }

        val settingsButton: ImageView = findViewById(R.id.settingIcon)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsFormActivity::class.java))
        }
    }
}