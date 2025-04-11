package com.pwrpower.apk.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R
import com.pwrpower.apk.api.CheckEmailResponse
import com.pwrpower.apk.api.EmailModel
import com.pwrpower.apk.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_email)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener { checkEmail() }
    }

    private fun checkEmail(){
        val emailText: TextInputEditText = findViewById(R.id.emailEditText)

        if (emailText.text.toString().isEmpty()) { // is not empty
            emailText.error = getString(R.string.email_error_1)
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText.text.toString()).matches()) { // is valid
            emailText.error = getString(R.string.email_error_2)
            return
        }

        RetrofitInstance.api.checkEmail(EmailModel(emailText.text.toString())).enqueue(object : Callback<CheckEmailResponse> {
                override fun onResponse(call: Call<CheckEmailResponse>, response: Response<CheckEmailResponse>) {
                    if (response.isSuccessful) {
                        val checkEmailResponse = response.body()
                        if (checkEmailResponse != null) {
                            if (checkEmailResponse.success && !checkEmailResponse.exists) {
                                val intent = Intent(this@RegisterEmailActivity, RegisterPasswordActivity::class.java)
                                intent.putExtra("Email", emailText.text.toString())
                                startActivity(intent)
                            } else {
                                emailText.error = getString(R.string.email_error_3)
                            }
                        }
                    } else {
                        Log.d("RegisterEmailActivity", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CheckEmailResponse>, t: Throwable) {
                    Log.d("RegisterEmailActivity", "Failure: ${t.message}")
                }
            }
        )
    }
}