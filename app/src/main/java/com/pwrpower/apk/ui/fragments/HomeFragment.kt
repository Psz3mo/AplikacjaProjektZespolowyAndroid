package com.pwrpower.apk.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.constraintlayout.widget.ConstraintLayout
import com.pwrpower.apk.R
import com.pwrpower.apk.api.BalanceResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.UserDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        downloadUserData(view)
        downloadUserBalance(view)

        val balance = view.findViewById<ConstraintLayout>(R.id.balanceAdd)
        balance.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, BalanceFragment()).addToBackStack(null).commit()
        }

        return view
    }

    private fun downloadUserBalance(view: View) {

        val preferences = activity?.getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val userId: Int = preferences?.getInt("accountId", 0) ?: 0

        RetrofitInstance.api.getUserBalance(userId).enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    val balanceResponse = response.body()
                    if (balanceResponse != null) {
                        val balanceTextView = view.findViewById<TextView>(R.id.userMoneyAmount)
                        val formatter = DecimalFormat("0.00")
                        val formattedBalance = formatter.format(balanceResponse.balance)
                        balanceTextView.text = "${formattedBalance} zł"
                    }
                    else {
                        Toast.makeText(this@HomeFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@HomeFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                Toast.makeText(this@HomeFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                            val nameTextView = view.findViewById<TextView>(R.id.mainUsername)
                            val name = userDataResponse.user?.name ?: ""
                            nameTextView.text = "Hello,\n$name"
                        }
                        else{
                            Toast.makeText(this@HomeFragment.requireContext(), userDataResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@HomeFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                Toast.makeText(this@HomeFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}