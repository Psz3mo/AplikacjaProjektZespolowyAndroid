package com.pwrpower.apk.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pwrpower.apk.R
import com.pwrpower.apk.ui.main.MainActivity

class SummaryRentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_summary_rent, container, false)

        val carName = arguments?.getString("carName")
        val carPrice = arguments?.getDouble("carCost")
        val startDateTime = arguments?.getString("startDateTime")
        val endDateTime = arguments?.getString("endDateTime")
        val minutes = arguments?.getInt("minutes")
        val fullCarCost = arguments?.getDouble("fullCarCost")
        val transaction = arguments?.getInt("payment")

        val carNameTextView = view.findViewById<TextView>(R.id.carName)
        val carPriceTextView = view.findViewById<TextView>(R.id.carPrice)
        val startDateTimeTextView = view.findViewById<TextView>(R.id.dateFrom)
        val endDateTimeTextView = view.findViewById<TextView>(R.id.dateTo)
        val total = view.findViewById<TextView>(R.id.totalPrice)
        val transactionTextView = view.findViewById<TextView>(R.id.paymentStatus)
        val paymentError = view.findViewById<TextView>(R.id.paymentError)

        carNameTextView.text = carName ?: "Unknown Car"
        carPriceTextView.text = carPrice?.let { "$it zł / min" } ?: "Unknown Price"
        startDateTimeTextView.text = startDateTime ?: "Unknown Start Time"
        endDateTimeTextView.text = endDateTime ?: "Unknown End Time"
        total.text = fullCarCost?.let { "$it zł ($minutes minutes)" } ?: "Unknown Total Price"
        transactionTextView.text = when (transaction) {
            0 -> "Payment rejected"
            1 -> "Payment accepted"
            else -> "Unknown transaction status"
        }
        if (transaction == 0) {
            paymentError.visibility = View.VISIBLE
        } else {
            paymentError.visibility = View.GONE
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