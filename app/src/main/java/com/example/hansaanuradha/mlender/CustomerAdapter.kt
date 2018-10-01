package com.example.hansaanuradha.mlender

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.customer_row.view.*

class CustomerAdapter(options: FirestoreRecyclerOptions<Customer>) : FirestoreRecyclerAdapter<Customer, CustomerViewHolder>(options) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomerViewHolder {
        val customerRow = LayoutInflater.from(p0.context).inflate(R.layout.customer_row, p0, false)
        return CustomerViewHolder(customerRow)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int, model: Customer) {
        holder.itemView.customerNameTextView.text = model.fname + " " + model.lname
    }
}

class CustomerViewHolder(view : View) : RecyclerView.ViewHolder(view){

}