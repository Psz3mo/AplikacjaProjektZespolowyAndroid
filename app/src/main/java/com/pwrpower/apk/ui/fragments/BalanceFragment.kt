package com.pwrpower.apk.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.BalanceResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.api.TransactionsResponse
import com.pwrpower.apk.data.TransactionAdapter
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.text.format
import java.text.DecimalFormat

class BalanceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_balance, container, false)

        downloadUserBalance(view)
        downloadTransactions(view)

        val toolbar: Toolbar = view.findViewById(R.id.balanceSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@BalanceFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val addFundsButton = view.findViewById<View>(R.id.addFunds)
        addFundsButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.container, AddFundsFragment()).addToBackStack(null).commit()
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

    private fun downloadUserBalance(view: View) {

        val preferences = activity?.getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val userId: Int = preferences?.getInt("accountId", 0) ?: 0

        RetrofitInstance.api.getUserBalance(userId).enqueue(object : Callback<BalanceResponse>{
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    val balanceResponse = response.body()
                    if (balanceResponse != null) {
                        val balanceTextView = view.findViewById<TextView>(R.id.balanceText)
                        val formatter = DecimalFormat("0.00")
                        val formattedBalance = formatter.format(balanceResponse.balance)
                        balanceTextView.text = "${formattedBalance} zł"
                    }
                    else {
                        Toast.makeText(this@BalanceFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@BalanceFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                Toast.makeText(this@BalanceFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun downloadTransactions(view: View) {
        val preferences = activity?.getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE)
        val userId: Int = preferences?.getInt("accountId", 0) ?: 0

        RetrofitInstance.api.getUserTransactions(userId).enqueue(object : Callback<TransactionsResponse> {
            override fun onResponse(call: Call<TransactionsResponse>, response: Response<TransactionsResponse>) {
                if (response.isSuccessful) {
                    val transactions = response.body()
                    if (transactions != null) {
                        val transactionAdapter = TransactionAdapter(transactions.transactions!!)
                        val recyclerView = view.findViewById<RecyclerView>(R.id.transactionsRecyclerView)
                        recyclerView.adapter = null
                        recyclerView.adapter = transactionAdapter
                    } else {
                        Toast.makeText(this@BalanceFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BalanceFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                Toast.makeText(this@BalanceFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}