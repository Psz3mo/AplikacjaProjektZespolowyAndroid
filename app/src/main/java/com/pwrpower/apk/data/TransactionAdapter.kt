package com.pwrpower.apk.data

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.Transaction
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.transactionAmount)
        val dateText: TextView = itemView.findViewById(R.id.transactionDate)
        val icon: ImageView = itemView.findViewById(R.id.transactionIcon)
        val description: TextView = itemView.findViewById(R.id.transactionDescription)
        val status: TextView = itemView.findViewById(R.id.transactionStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val formatter = DecimalFormat("0.00")
        val formattedAmount = formatter.format(transaction.amount)
        holder.amountText.text = "$formattedAmount zł"
        holder.description.text = transaction.description

        if (transaction.status == 1) {
            holder.status.text = ""
            if(transaction.type == "upload") {
                holder.icon.setImageResource(R.drawable.dollar_icon)
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.green_icon), PorterDuff.Mode.SRC_IN)
            } else {
                holder.icon.setImageResource(R.drawable.car_icon)
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.onBackground), PorterDuff.Mode.SRC_IN)
            }
        } else {
            holder.status.text = "Rejected"
            holder.icon.setImageResource(R.drawable.danger_icon)
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red_icon), PorterDuff.Mode.SRC_IN)
        }

        val zoned = ZonedDateTime.parse(transaction.created_at)
        val formatter2 = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
        val formatted = zoned.format(formatter2)
        holder.dateText.text = formatted
    }

    override fun getItemCount(): Int = transactions.size
}