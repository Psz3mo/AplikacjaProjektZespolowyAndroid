package com.pwrpower.apk.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.pwrpower.apk.R
import androidx.core.content.edit
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UserDataResponse
import com.pwrpower.apk.ui.auth.AuthActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        downloadUserData(view)

        sharedPreferences = this@SettingsFragment.requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)

        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)

        val switchThemeLayout: ConstraintLayout = view.findViewById(R.id.settingsLayoutTheme)
        val switchTheme: SwitchCompat = view.findViewById(R.id.settingsThemeSwitch)

        switchTheme.isChecked = isDarkMode

        switchThemeLayout.setOnClickListener {
            switchTheme.isChecked = !switchTheme.isChecked
            switchTheme.performClick()
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            with(sharedPreferences.edit()) {
                putBoolean("DARK_MODE", isChecked)
                apply()
            }
        }

        val editProfileButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutEditUser)
        editProfileButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, EditProfileFragment()).addToBackStack(null).commit()
        }

        val changePasswordButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutChangePassword)
        changePasswordButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, ChangePasswordFragment()).addToBackStack(null).commit()
        }

        val changeEmailButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutChangeEmail)
        changeEmailButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, ChangeEmailFragment()).addToBackStack(null).commit()
        }

        val balanceButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutBalance)
        balanceButton.setOnClickListener {
            //parentFragmentManager.beginTransaction().replace(R.id.container, LanguageFragment()).addToBackStack(null).commit()
        }

        val historyButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutHistory)
        historyButton.setOnClickListener {
            //parentFragmentManager.beginTransaction().replace(R.id.container, LanguageFragment()).addToBackStack(null).commit()
        }

        val languageButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutLanguage)
        languageButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, LanguageFragment()).addToBackStack(null).commit()
        }

        val logoutButton = view.findViewById<ConstraintLayout>(R.id.settingsLayoutLogout)
        logoutButton.setOnClickListener {
            val preferences = this@SettingsFragment.requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
            preferences.edit { putBoolean("isLoggedIn", false) }
            startActivity(Intent(this@SettingsFragment.requireActivity(), AuthActivity::class.java))
            this@SettingsFragment.requireActivity().finish()
        }


        return view
    }

    private fun downloadUserData(view: View) {
        val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userId: Int = preferences?.getInt("accountId", 0) ?: 0

        RetrofitInstance.api.getUserData(userId).enqueue(object : Callback<UserDataResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<UserDataResponse>,
                response: Response<UserDataResponse>
            ) {
                if(response.isSuccessful){
                    val userDataResponse = response.body()
                    if (userDataResponse != null) {
                        if(userDataResponse.success){
                            val nameTextView = view.findViewById<TextView>(R.id.mainNameTextView)
                            val name = userDataResponse.user?.name ?: ""
                            val surname = userDataResponse.user?.surname ?: ""
                            nameTextView.text = "$name $surname"
                        }
                        else{
                            Toast.makeText(this@SettingsFragment.requireContext(), userDataResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@SettingsFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                Toast.makeText(this@SettingsFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}