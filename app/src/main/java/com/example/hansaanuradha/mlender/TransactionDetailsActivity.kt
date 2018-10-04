package com.example.hansaanuradha.mlender

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_transaction_details.*
import java.text.SimpleDateFormat
import android.util.Log
import java.util.*


class TransactionDetailsActivity : AppCompatActivity() {

    // FireStore Reference
    var db : FirebaseFirestore?= null
    var customerReference : DocumentReference?= null
    var transactionRef : CollectionReference?= null

    // Progress Bar
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transaction Details"

        db = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(this)

        // Show Dialog box
        dialog?.setMessage("Loading, please wait.")
        dialog?.show()

        val fullName = intent.getStringExtra("fullname")
        val startDateString : String = intent.getStringExtra("startDate")
        val amount : Double = java.lang.Double.parseDouble(intent.getStringExtra("amount"))

        getCustomerDetails(db, fullName)

        getTransactionDetails(db, fullName, startDateString, amount)

        
    }

    private fun getCustomerDetails(db : FirebaseFirestore ?= null, fullName : String){
        customerReference = db?.collection("customers")?.document(fullName)

        customerReference?.get()
                ?.addOnSuccessListener(OnSuccessListener<DocumentSnapshot>
                { documentSnapshot -> val customer = documentSnapshot.toObject<Customer>(Customer::class.java!!)
                    val address = customer?.addressNo + ", " + customer?.street + ", " + customer?.city
                    getFullNameTextView.text = "Full Name : $fullName"
                    getAddressTextView.text = "Address : $address"
                    getContactNumberTextView.text = "Contact Number : " + customer?.contactNumber
                })
    }

    private fun getTransactionDetails(db : FirebaseFirestore ?= null, fullName : String, startDateString : String, amount : Double){
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val startDate = dateFormat.parse(startDateString)

        transactionRef = db?.collection("transactions")

        transactionRef
                ?.whereEqualTo("from", fullName)
                ?.whereEqualTo("startDate", startDate)
                ?.whereEqualTo("initialAmount", amount)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var formatter : SimpleDateFormat?= null
                        for (document in task.result) {

                            formatter = SimpleDateFormat("dd/M/yyyy")

                            Log.d("result", document.id + " => " + document.data)
                            val transaction = document.toObject(Transaction::class.java)

                            val startDateString : String = formatter.format(transaction.startDate)
                            val endDateString : String = formatter.format(transaction.endDate)
                            var transactionType : String ?= null
                            transactionType = if(transaction.onlyInterest as Boolean)
                                "Only Interest"
                            else
                                "Installment Payment"

                            val status = if (transaction.completed as Boolean)
                                "Completed"
                            else
                                "Not Completed"

                            getAmountTextView.text = "Amount : " + transaction.initialAmount
                            getRemainingAmountTextView.text = "Remaining Amount : " + transaction.remainingAmount.toString()
                            getInterestRateTextView.text = "Interest Rate : " + transaction.interestRate.toString()
                            getMonthlyInterestAmountTextView.text = "Monthly Interest : " + transaction.interestToRecieve.toString()
                            getTotalProfitTextView.text = "Total Profit : " + transaction.totalProfit.toString()
                            getStartDateTextView.text = "Start Date : $startDateString"
                            getEndDateTextView.text = "End Date : $endDateString"
                            transactionTypeTextView.text = "Transaction Type : $transactionType"
                            getStatusTextView.text = "Status : $status"

                            // Dismiss Dialog Box
                            dialog?.dismiss()
                        }
                    } else {
                        Log.d("result", "Error getting documents: ", task.exception)
                        // Dismiss Dialog Box
                        dialog?.dismiss()
                    }
                }
    }


}
