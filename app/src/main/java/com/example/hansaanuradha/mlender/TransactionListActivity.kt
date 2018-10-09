package com.example.hansaanuradha.mlender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_customer.*

class TransactionListActivity : AppCompatActivity() {

    // FireStore Ref
    private var db : FirebaseFirestore?= null
    // FireStore Adapter
    private var adapter : TransactionAdaper ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "TRANSACTIONS"

        val fullName = intent.getStringExtra("fullname")

        Log.i("name", fullName)

        customerListRecyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        val query : Query = db?.collection("transactions")!!
                .whereEqualTo("from", fullName)
                .orderBy("startDate", Query.Direction.DESCENDING)

        val options : FirestoreRecyclerOptions<Transaction> = FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query, Transaction::class.java)
                .build()
        adapter = TransactionAdaper(options)
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
