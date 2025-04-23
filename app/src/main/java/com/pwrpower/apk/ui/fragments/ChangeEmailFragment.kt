package com.pwrpower.apk.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
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
import com.pwrpower.apk.api.ChangeEmailResponse
import com.pwrpower.apk.api.NewEmailModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UserDataResponse
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChangeEmailFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_email, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.changeEmailSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@ChangeEmailFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        downloadUserData(view)

        val saveButton = view.findViewById<View>(R.id.saveButton)

        saveButton.setOnClickListener {
            val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val userId: Int = preferences?.getInt("accountId", 0) ?: 0

            val email = view.findViewById<TextInputEditText>(R.id.emailEditText).text.toString()

            RetrofitInstance.api.changeEmail(userId, NewEmailModel(email)).enqueue(object : Callback<ChangeEmailResponse> {
                override fun onResponse(
                    call: Call<ChangeEmailResponse>,
                    response: Response<ChangeEmailResponse>
                ) {
                    if (response.isSuccessful) {
                        val changeEmailResponse = response.body()
                        if (changeEmailResponse != null) {
                            if (changeEmailResponse.success) {
                                Toast.makeText(this@ChangeEmailFragment.requireContext(), getString(R.string.change_email_successful), Toast.LENGTH_SHORT).show()
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                            else{
                                Toast.makeText(this@ChangeEmailFragment.requireContext(), changeEmailResponse.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ChangeEmailFragment.requireContext(), getString(R.string.change_email_error), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChangeEmailResponse>, t: Throwable) {
                    Toast.makeText(this@ChangeEmailFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

        return view
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
                            val emailInput: TextInputEditText = view.findViewById(R.id.emailEditText)
                            emailInput.setText(userDataResponse.user?.email ?: "")
                        }
                        else{
                            Toast.makeText(this@ChangeEmailFragment.requireContext(), userDataResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@ChangeEmailFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                Toast.makeText(this@ChangeEmailFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
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