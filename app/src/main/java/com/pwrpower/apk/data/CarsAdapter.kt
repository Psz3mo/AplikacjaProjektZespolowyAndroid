package com.pwrpower.apk.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.Cars

class CarsAdapter(private val cars: List<Cars>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<CarsAdapter.CarsViewHolder>() {

    class CarsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carName: TextView = itemView.findViewById(R.id.carName)
        val carPrice: TextView = itemView.findViewById(R.id.costPerMinute)
        val photo: ImageView = itemView.findViewById(R.id.carPhoto)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val ratingText: TextView = itemView.findViewById(R.id.noRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car_search, parent, false)
        return CarsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarsViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick(cars[position].id.toString())
        }
        val car = cars[position]
        holder.carName.text = car.name
        holder.carPrice.text = holder.itemView.context.getString(R.string.cost1) + " " + car.price.toString() + " " + holder.itemView.context.getString(R.string.cost2)
        if(car.rating == 0.0) {
            holder.ratingText.visibility = View.VISIBLE
            holder.rating.visibility = View.GONE
        } else {
            holder.ratingText.visibility = View.GONE
            holder.rating.visibility = View.VISIBLE
            holder.rating.rating = car.rating.toFloat()
        }
        //photo loading
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    fun getItem(position: Int): Cars {
        return cars[position]
    }

    fun getCarId(position: Int): Int {
        return cars[position].id
    }
}