package com.pwrpower.apk.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R
import com.pwrpower.apk.api.CheckPhoneResponse
import com.pwrpower.apk.api.PhoneModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UpdateUserResponse
import com.pwrpower.apk.api.UserDataModel
import com.pwrpower.apk.api.UserDataResponse
import com.pwrpower.apk.ui.auth.RegisterSummaryActivity
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var phoneNumber: String // Variable to store the phone number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.editProfileSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@EditProfileFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // download user data
        downloadUserData(view)

        val nameInput: TextInputEditText = view.findViewById(R.id.nameEditText)
        val surnameInput: TextInputEditText =  view.findViewById(R.id.lastnameEditText)
        val phoneInput: TextInputEditText =  view.findViewById(R.id.phoneEditText)
        val licenseNumberInput: TextInputEditText =  view.findViewById(R.id.licenseNumberEditText)
        val licenseExpInput: TextInputEditText =  view.findViewById(R.id.licenseExpEditText)

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

            val datePicker = DatePickerDialog(this@EditProfileFragment.requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                licenseExpInput.setText(selectedDate)
            }, year, month, day)
            datePicker.datePicker.minDate = calendar.timeInMillis + 24 * 60 * 60 * 1000

            datePicker.show()
        }


        val saveButton = view.findViewById<View>(R.id.saveButton)
        saveButton.setOnClickListener {
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
                if (!isAvailable && phone != phoneNumber) {
                    phoneInput.error = getString(R.string.phone_error_taken)
                    return@checkPhone
                }
                else{
                    if (licenseNumber.isEmpty()) {
                        licenseNumberInput.error = getString(R.string.license_number_error)
                        return@checkPhone
                    }

                    if (!licenseExpDate.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}"))) {
                        licenseExpInput.error = getString(R.string.license_exp_error)
                        return@checkPhone
                    }

                    val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
                    val userId: Int = preferences?.getInt("accountId", 0) ?: 0

                    val userData = UserDataModel(
                        name,
                        surname,
                        phone,
                        licenseNumber,
                        convertDate(licenseExpDate)
                    )

                    RetrofitInstance.api.updateUserData(userId, userData).enqueue(object : Callback<UpdateUserResponse> {
                        override fun onResponse(
                            call: Call<UpdateUserResponse>,
                            response: Response<UpdateUserResponse>
                        ) {
                            if(response.isSuccessful){
                                val updateUserResponse = response.body()
                                if(updateUserResponse != null){
                                    if(updateUserResponse.success){
                                        Toast.makeText(this@EditProfileFragment.requireContext(), getString(R.string.edit_profile_success), Toast.LENGTH_SHORT).show()
                                        activity?.onBackPressedDispatcher?.onBackPressed()
                                    }
                                    else{
                                        Toast.makeText(this@EditProfileFragment.requireContext(), updateUserResponse.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(this@EditProfileFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                            Toast.makeText(this@EditProfileFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
        return view
    }

    private fun checkPhone(phone: String, callback: (Boolean) -> Unit) {
        RetrofitInstance.api.checkPhone(PhoneModel(phone)).enqueue(object :
            Callback<CheckPhoneResponse> {
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
                    Toast.makeText(
                        this@EditProfileFragment.requireContext(),
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<CheckPhoneResponse>, t: Throwable) {
                Toast.makeText(
                    this@EditProfileFragment.requireContext(),
                    "Failure: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false)
            }
        })
    }

    private fun downloadUserData(view: View) {
        val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userId: Int = preferences?.getInt("accountId", 0) ?: 0

        RetrofitInstance.api.getUserData(userId).enqueue(object : Callback<UserDataResponse> {
            override fun onResponse(
                call: Call<UserDataResponse>,
                response: Response<UserDataResponse>
            ) {
                if(response.isSuccessful){
                    val userDataResponse = response.body()
                    if (userDataResponse != null) {
                        if(userDataResponse.success){
                            val nameInput: TextInputEditText = view.findViewById(R.id.nameEditText)
                            val surnameInput: TextInputEditText =  view.findViewById(R.id.lastnameEditText)
                            val phoneInput: TextInputEditText =  view.findViewById(R.id.phoneEditText)
                            val licenseNumberInput: TextInputEditText =  view.findViewById(R.id.licenseNumberEditText)
                            val licenseExpInput: TextInputEditText =  view.findViewById(R.id.licenseExpEditText)

                            nameInput.setText(userDataResponse.user?.name ?: "")
                            surnameInput.setText(userDataResponse.user?.surname ?: "")
                            phoneInput.setText(userDataResponse.user?.phone ?: "")
                            phoneNumber = userDataResponse.user?.phone ?: ""
                            licenseNumberInput.setText(userDataResponse.user?.licenseNumber ?: "")

                            val isoDate = userDataResponse.user?.licenseExpiration

                            val parsedDate = OffsetDateTime.parse(isoDate)
                            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())

                            val formattedDate = parsedDate.format(formatter)

                            licenseExpInput.setText(formattedDate)
                        }
                        else{
                            Toast.makeText(this@EditProfileFragment.requireContext(), userDataResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@EditProfileFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                Toast.makeText(this@EditProfileFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
    }
}