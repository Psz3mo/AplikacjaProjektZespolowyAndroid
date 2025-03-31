package com.pwrpower.apk.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R
import java.util.Calendar

class RegisterUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            startActivity(Intent(this, RegisterSummaryActivity::class.java))
        }

        val licenseExpDate: TextInputEditText = findViewById(R.id.licenseExpEditText)
        licenseExpDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                licenseExpDate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val licenseNumber: TextInputEditText = findViewById(R.id.licenseNumberEditText)
        licenseNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return
                isEditing = true

                var formatted = s.toString().replace("/", "")
                if (formatted.length > 5) formatted = formatted.substring(0, 5) + "/" + formatted.substring(5)
                if (formatted.length > 8) formatted = formatted.substring(0, 8) + "/" + formatted.substring(8)
                if (formatted.length > 13) formatted = formatted.substring(0, 13)
                licenseNumber.setText(formatted)
                licenseNumber.setSelection(formatted.length)

                isEditing = false
            }
        })

        val phoneNumber: TextInputEditText = findViewById(R.id.phoneEditText)
        phoneNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return
                isEditing = true

                var formatted = s.toString().replace("[^\\d+]".toRegex(), "")
                if (!formatted.startsWith("+") && formatted.isNotEmpty()) {
                    formatted = "+$formatted"
                }
                if (formatted.length > 3) formatted = formatted.substring(0, 3) + " " + formatted.substring(3)
                if (formatted.length > 7) formatted = formatted.substring(0, 7) + " " + formatted.substring(7)
                if (formatted.length > 11) formatted = formatted.substring(0, 11) + " " + formatted.substring(11)
                if (formatted.length > 16) formatted = formatted.substring(0, 16)
                phoneNumber.setText(formatted)
                phoneNumber.setSelection(formatted.length)

                isEditing = false
            }
        })
    }
}