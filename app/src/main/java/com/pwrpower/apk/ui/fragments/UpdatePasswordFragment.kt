package com.pwrpower.apk.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.pwrpower.apk.R
import com.pwrpower.apk.api.ChangePasswordResponse
import com.pwrpower.apk.api.NewPasswordModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_password, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.updatePasswordSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@UpdatePasswordFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val saveButton: View = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {

            val password = view.findViewById<TextInputEditText>(R.id.passwordEditText).text.toString()
            val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val userId: Int = preferences?.getInt("accountId", 0) ?: 0

            RetrofitInstance.api.changePassword(userId, NewPasswordModel(password)).enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val changePasswordResponse = response.body()
                        if (changePasswordResponse != null && changePasswordResponse.success) {
                            Toast.makeText(requireContext(), getString(R.string.password_change_success), Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            parentFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment()).commit()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.password_change_error), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val passwordEditText: TextInputEditText = view.findViewById(R.id.passwordEditText)
        val req1: TextView = view.findViewById(R.id.passwordReq1)
        val req2: TextView = view.findViewById(R.id.passwordReq2)
        val req3: TextView = view.findViewById(R.id.passwordReq3)
        val req4: TextView = view.findViewById(R.id.passwordReq4)
        val req5: TextView =view. findViewById(R.id.passwordReq5)

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
            saveButton.isEnabled = allValid
        }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordRequirements(s.toString())
            }
        })

        return view
    }

    private fun updateRequirement(textView: TextView, isMet: Boolean) {
        if (isMet) {
            textView.setTextColor(ContextCompat.getColor(this@UpdatePasswordFragment.requireContext(), R.color.green_check))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check_icon, 0, 0, 0)
        } else {
            textView.setTextColor(ContextCompat.getColor(this@UpdatePasswordFragment.requireContext(), R.color.red_error))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_icon, 0, 0, 0)
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