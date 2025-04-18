package com.pwrpower.apk.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R
import com.pwrpower.apk.api.CheckPhoneResponse
import com.pwrpower.apk.api.PhoneModel
import com.pwrpower.apk.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        val nameInput: TextInputEditText = findViewById(R.id.nameEditText)
        val surnameInput: TextInputEditText = findViewById(R.id.lastnameEditText)
        val phoneInput: TextInputEditText = findViewById(R.id.phoneEditText)
        val licenseNumberInput: TextInputEditText = findViewById(R.id.licenseNumberEditText)
        val licenseExpInput: TextInputEditText = findViewById(R.id.licenseExpEditText)

        phoneInput.setText("+48")
        phoneInput.text?.let { phoneInput.setSelection(it.length) }

        val toolbar = findViewById<Toolbar>(R.id.userToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.onBackground))

        nameInput.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                val raw = s.toString().replace("[^\\p{L}]".toRegex(), "")
                val formatted = raw.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                if (s.toString() == formatted) return

                isEditing = true
                nameInput.setText(formatted)
                nameInput.setSelection(formatted.length)
                isEditing = false
            }
        })


        surnameInput.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                val raw = s.toString().replace("[^\\p{L}]".toRegex(), "")
                val formatted = raw.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                if (s.toString() == formatted) return

                isEditing = true
                surnameInput.setText(formatted)
                surnameInput.setSelection(formatted.length)
                isEditing = false
            }
        })



        phoneInput.addTextChangedListener(object : TextWatcher {
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
                phoneInput.setText(formatted)
                phoneInput.setSelection(formatted.length)

                isEditing = false
            }
        })

        licenseNumberInput.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return
                isEditing = true

                var raw = s.toString().uppercase().replace("[^A-Z0-9]".toRegex(), "")

                if (raw.length > 5) raw = raw.substring(0, 5) + "/" + raw.substring(5)
                if (raw.length > 8) raw = raw.substring(0, 8) + "/" + raw.substring(8)
                if (raw.length > 13) raw = raw.substring(0, 13)

                licenseNumberInput.setText(raw)
                licenseNumberInput.setSelection(raw.length)

                isEditing = false
            }
        })


        licenseExpInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                licenseExpInput.setText(selectedDate)
            }, year, month, day)
            datePicker.datePicker.minDate = calendar.timeInMillis + 24 * 60 * 60 * 1000

            datePicker.show()
        }

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {

            val name: String = nameInput.text.toString()
            val surname: String = surnameInput.text.toString()
            val phone: String = phoneInput.text.toString()
            val licenseNumber: String = licenseNumberInput.text.toString()
            val licenseExpDate: String = licenseExpInput.text.toString()

            if (name.length < 2) {
                nameInput.error = getString(R.string.name_error)
                return@setOnClickListener
            }

            if (surname.length < 2) {
                surnameInput.error = getString(R.string.surname_error)
                return@setOnClickListener
            }

            if (!phone.matches(Regex("^\\+?[0-9\\s]{9,16}$"))) {
                phoneInput.error = getString(R.string.phone_error)
                return@setOnClickListener
            }
            checkPhone(phone) { isAvailable ->
                if (!isAvailable) {
                    phoneInput.error = getString(R.string.phone_error_taken)
                }
            }

            if (licenseNumber.isEmpty()) {
                licenseNumberInput.error = getString(R.string.license_number_error)
                return@setOnClickListener
            }

            if (!licenseExpDate.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}"))) {
                licenseExpInput.error = getString(R.string.license_exp_error)
                return@setOnClickListener
            }

            val email = intent.getStringExtra("Email")
            val password = intent.getStringExtra("Password")

            val intent = Intent(this, RegisterSummaryActivity::class.java)
            intent.putExtra("Email", email)
            intent.putExtra("Password", password)
            intent.putExtra("Name", name)
            intent.putExtra("Surname", surname)
            intent.putExtra("Phone", phone)
            intent.putExtra("LicenseNumber", licenseNumber)
            intent.putExtra("LicenseExpDate", licenseExpDate)
            startActivity(intent)
        }
    }

    private fun checkPhone(phone: String, callback: (Boolean) -> Unit) {
        RetrofitInstance.api.checkPhone(PhoneModel(phone)).enqueue(object : Callback<CheckPhoneResponse> {
            override fun onResponse(
                call: Call<CheckPhoneResponse>,
                response: Response<CheckPhoneResponse>
            ) {
                if (response.isSuccessful) {
                    val checkPhoneResponse = response.body()
                    if (checkPhoneResponse != null) {
                        if (checkPhoneResponse.success && !checkPhoneResponse.exists) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    Toast.makeText(this@RegisterUserActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<CheckPhoneResponse>, t: Throwable) {
                Toast.makeText(this@RegisterUserActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}