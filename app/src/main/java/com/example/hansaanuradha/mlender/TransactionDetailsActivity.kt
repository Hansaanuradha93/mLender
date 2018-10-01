package com.example.hansaanuradha.mlender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class TransactionDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transaction Details"

        val fullName = intent.getStringExtra("fullname")
        Toast.makeText(this@TransactionDetailsActivity, fullName, Toast.LENGTH_SHORT).show()

    }
}
