package com.pwrpower.apk.ui.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pwrpower.apk.R
import com.pwrpower.apk.api.GetReviewsResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.data.ReviewsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var reviewsAdapter: ReviewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_reviews, container, false)

        downloadReviews(view)

        return view
    }

    private fun downloadReviews(view: View) {
        RetrofitInstance.api.getReviews(arguments?.getString("carId")?.toInt() ?: 0).enqueue(object :
            Callback<GetReviewsResponse> {
            override fun onResponse(call: Call<GetReviewsResponse>, response: Response<GetReviewsResponse>) {
                if (response.isSuccessful) {
                    val reviews = response.body()
                    if (reviews != null) {
                        val reviewsList = reviews.reviews
                        if (reviewsList != null) {
                            if (reviewsList.isNotEmpty()) {
                                val recyclerView = view.findViewById<RecyclerView>(R.id.reviewsRecyclerView)
                                reviewsAdapter = ReviewsAdapter(reviewsList)
                                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                recyclerView.adapter = reviewsAdapter
                                recyclerView.visibility = View.VISIBLE
                                val noReviewsText = view.findViewById<View>(R.id.emptyTextView)
                                noReviewsText.visibility = View.GONE
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Reviews are null", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetReviewsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}