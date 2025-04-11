package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R

class RegisterPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val passwordEditText: TextInputEditText = findViewById(R.id.passwordEditText)
        val req1: TextView = findViewById(R.id.passwordReq1)
        val req2: TextView = findViewById(R.id.passwordReq2)
        val req3: TextView = findViewById(R.id.passwordReq3)
        val req4: TextView = findViewById(R.id.passwordReq4)
        val req5: TextView = findViewById(R.id.passwordReq5)

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.isEnabled = false
        nextButton.setOnClickListener {
            val email = intent.getStringExtra("Email")
            val intent = Intent(this, RegisterUserActivity::class.java)
            intent.putExtra("Email", email)
            intent.putExtra("Password", passwordEditText.text.toString())
            startActivity(intent)
        }

        fun checkPasswordRequirements(password: String) {
            val condition1 = password.length >= 8
            val condition2 = password.any { it.isDigit() }
            val condition3 = password.any { it.isUpperCase() }
            val condition4 = password.any { it.isLowerCase() }
            val condition5 = password.any { "!@#\$%^&*(),.?\":{}|<>+-;".contains(it) }

            updateRequirement(req1, condition1)
            updateRequirement(req2, condition2)
            updateRequirement(req3, condition3)
            updateRequirement(req4, condition4)
            updateRequirement(req5, condition5)

            val allValid = condition1 && condition2 && condition3 && condition4 && condition5
            nextButton.isEnabled = allValid
        }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordRequirements(s.toString())
            }
        })
    }

    private fun updateRequirement(textView: TextView, isMet: Boolean) {
        if (isMet) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.green_check))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check_icon, 0, 0, 0)
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.red_error))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_icon, 0, 0, 0)
        }
    }
}