package com.example.hansaanuradha.mlender

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.customer_row.view.*
import kotlinx.android.synthetic.main.transaction_row.view.*

class TransactionAdaper(options: FirestoreRecyclerOptions<Transaction>) : FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>(options) {
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int, model: Transaction) {
        holder?.itemView.amountTextView.text = model.initialAmount.toString()
        holder?.itemView.hiddenFullName.text = model.from
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
            view.context.startActivity(intent)
        }
    }
}