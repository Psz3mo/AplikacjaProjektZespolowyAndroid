package com.pwrpower.apk.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.Review

class ReviewsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.review_username)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBarComment)
        val reviewText: TextView = itemView.findViewById(R.id.review_text)
        val reviewDate: TextView = itemView.findViewById(R.id.review_date)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val review = reviews[position]
        holder.username.text = review.name
        holder.rating.rating = review.rating.toFloat()
        holder.reviewText.text = review.review
        holder.reviewDate.text = review.date
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

}