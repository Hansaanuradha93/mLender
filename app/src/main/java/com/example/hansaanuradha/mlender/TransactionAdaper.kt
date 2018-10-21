package com.example.hansaanuradha.mlender

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.transaction_row.view.*
import java.text.SimpleDateFormat
import java.util.*



class TransactionAdaper(options: FirestoreRecyclerOptions<Transaction>) : FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>(options) {
    private var formatter : SimpleDateFormat ?= null
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int, model: Transaction) {

        formatter = SimpleDateFormat("dd/M/yyyy")
        val startDateString : String = formatter!!.format(model.startDate)
        if(model.completed!!) {
            holder.itemView.amountTextView.setTextColor(Color.rgb(0,100,0))
            holder.itemView.amountTextView.text = "Amount : " + model.initialAmount.toString() + "\nDate : $startDateString || Completed"
        }
        else
            holder.itemView.amountTextView.text = "Amount : " + model.initialAmount.toString() + "\nDate : $startDateString"

        holder.itemView.hiddenFullName.text = model.from
        holder.itemView.hiddenAmount.text = model.initialAmount.toString()
        holder.itemView.hiddenStartDate.text = model.startDate.toString()

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TransactionViewHolder {
        val transactionRow = LayoutInflater.from(p0.context).inflate(R.layout.transaction_row, p0, false)
        return TransactionViewHolder(transactionRow)
    }


}

class TransactionViewHolder(view : View) : RecyclerView.ViewHolder(view){

    init{
        view.setOnClickListener {
            val intent = Intent(view.context, TransactionDetailsActivity::class.java)
            intent.putExtra("fullname", view.hiddenFullName.text)
            intent.putExtra("amount", view.hiddenAmount.text)
            intent.putExtra("startDate", view.hiddenStartDate.text)
            view.context.startActivity(intent)
        }
    }
}