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
import com.pwrpower.apk.api.PasswordModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.VerifyPasswordResponse
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.changePasswordSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@ChangePasswordFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val checkButton = view.findViewById<View>(R.id.checkButton)
        checkButton.setOnClickListener {

            val passwordInput = view.findViewById<TextInputEditText>(R.id.passwordEditText)
            val password = passwordInput.text.toString()
            if (password.isEmpty()) {
               passwordInput.error = getString(R.string.password_error_1)
                return@setOnClickListener
            }

            val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val userId: Int = preferences?.getInt("accountId", 0) ?: 0

            RetrofitInstance.api.verifyPassword(PasswordModel(userId, password)).enqueue(object :
                Callback<VerifyPasswordResponse> {
                override fun onResponse(
                    call: Call<VerifyPasswordResponse>,
                    response: Response<VerifyPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val verifyPasswordResponse = response.body()
                        if (verifyPasswordResponse != null && verifyPasswordResponse.success) {
                            parentFragmentManager.beginTransaction().replace(R.id.container, UpdatePasswordFragment()).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(this@ChangePasswordFragment.requireContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ChangePasswordFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VerifyPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
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