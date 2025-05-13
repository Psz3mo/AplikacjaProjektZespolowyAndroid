package com.pwrpower.apk.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.AddPaymentResponse
import com.pwrpower.apk.api.PaymentModel
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFundsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_funds, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.addFundsSettingsToolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this@AddFundsFragment.requireContext(), R.color.onBackground))
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val amountInput = view.findViewById<TextView>(R.id.textAmountDisplay)
        var amount = 0

        fun addAmount(sign: Int) {
            if (amount.toString().length >= 4)
                return
            amount = amount * 10 + sign
            if (amount > 9999)
                amount = 9999
            amountInput.text = "${amount} zł"
        }

        val spinner: Spinner = view.findViewById(R.id.spinnerPaymentMethod)
        val items = listOf(
            getString(R.string.payment_method_1),
            getString(R.string.payment_method_2),
            getString(R.string.payment_method_3),
            getString(R.string.payment_method_4),
            getString(R.string.payment_method_5))

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val keyboard1 = view.findViewById<Button>(R.id.keyboard1)
        val keyboard2 = view.findViewById<Button>(R.id.keyboard2)
        val keyboard3 = view.findViewById<Button>(R.id.keyboard3)
        val keyboard4 = view.findViewById<Button>(R.id.keyboard4)
        val keyboard5 = view.findViewById<Button>(R.id.keyboard5)
        val keyboard6 = view.findViewById<Button>(R.id.keyboard6)
        val keyboard7 = view.findViewById<Button>(R.id.keyboard7)
        val keyboard8 = view.findViewById<Button>(R.id.keyboard8)
        val keyboard9 = view.findViewById<Button>(R.id.keyboard9)
        val keyboard0 = view.findViewById<Button>(R.id.keyboard0)
        val keyboardBackspace = view.findViewById<Button>(R.id.keyboardBackspace)
        val keyboardOk = view.findViewById<Button>(R.id.keyboardOk)

        keyboard1.setOnClickListener { addAmount(1) }
        keyboard2.setOnClickListener { addAmount(2) }
        keyboard3.setOnClickListener { addAmount(3) }
        keyboard4.setOnClickListener { addAmount(4) }
        keyboard5.setOnClickListener { addAmount(5) }
        keyboard6.setOnClickListener { addAmount(6) }
        keyboard7.setOnClickListener { addAmount(7) }
        keyboard8.setOnClickListener { addAmount(8) }
        keyboard9.setOnClickListener { addAmount(9) }
        keyboard0.setOnClickListener { addAmount(0) }

        keyboardBackspace.setOnClickListener {
            amount /= 10
            amountInput.text = "${amount} zł"
        }

        keyboardOk.setOnClickListener {

            if (amount < 20) {
                Toast.makeText(this@AddFundsFragment.requireContext(), getString(R.string.add_funds_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val preferences: SharedPreferences? = activity?.getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val userId: Int = preferences?.getInt("accountId", 0) ?: 0
            RetrofitInstance.api.addPayment(userId, PaymentModel(amount.toDouble(), getString(R.string.add_funds_using) + " " + spinner.selectedItem.toString())).enqueue(object : Callback<AddPaymentResponse> {
                override fun onResponse(call: Call<AddPaymentResponse>, response: Response<AddPaymentResponse>) {
                    if (response.isSuccessful) {
                        val addPaymentResponse = response.body()
                        if (addPaymentResponse != null) {
                            if (addPaymentResponse.success) {
                                Toast.makeText(this@AddFundsFragment.requireContext(), getString(R.string.add_funds_success), Toast.LENGTH_SHORT).show()
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            } else {
                                Toast.makeText(this@AddFundsFragment.requireContext(), addPaymentResponse.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(this@AddFundsFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddFundsFragment.requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddPaymentResponse>, t: Throwable) {
                    Toast.makeText(this@AddFundsFragment.requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val cancelButton = view.findViewById<TextView>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
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