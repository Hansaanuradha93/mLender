package com.example.hansaanuradha.mlender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_customer.*

class CustomerActivity : AppCompatActivity() {
    // FireStore Ref
    private var db : FirebaseFirestore ?= null
    // FireStore Adapter
    private var adapter : CustomerAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CUSTOMERS"



        customerListRecyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        val query : Query = db?.collection("customers")!!.orderBy("fname", Query.Direction.ASCENDING)

        val options : FirestoreRecyclerOptions<Customer>  = FirestoreRecyclerOptions.Builder<Customer>()
                .setQuery(query, Customer::class.java)
                .build()
        adapter = CustomerAdapter(options)
        customerListRecyclerView.adapter = adapter


    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}
