package com.example.hansaanuradha.mlender

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_updated_transaction_details.*
import java.text.SimpleDateFormat
import android.util.Log
import android.view.View
import java.util.*


class TransactionDetailsActivity : AppCompatActivity() {

    // FireStore Reference
    var db : FirebaseFirestore?= null
    var customerReference : DocumentReference?= null
    var transactionRef : CollectionReference?= null

    // Progress Bar
    private var dialog: ProgressDialog? = null

    // Fields
    var fullName : String ?= null
    var startDateString : String ?= null
    var amount : Double ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updated_transaction_details)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transaction Details"

        db = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(this)

        // Show Dialog box
        dialog?.setMessage("Loading, please wait.")
        dialog?.show()

        fullName = intent.getStringExtra("fullname")
        startDateString = intent.getStringExtra("startDate")
        amount = java.lang.Double.parseDouble(intent.getStringExtra("amount"))

        getCustomerDetails(db, fullName)

        getTransactionDetails(db, fullName, startDateString, amount)


    }

    fun update(view : View){

        val intent = Intent(this@TransactionDetailsActivity, TransactionUpdateActivity::class.java)
        intent.putExtra("amount", amount)
        intent.putExtra("fullname", fullName)
        intent.putExtra("startDateString", startDateString)
        startActivity(intent)
    }

    private fun getCustomerDetails(db : FirebaseFirestore ?= null, fullName : String ?= null){
        customerReference = db?.collection("customers")?.document(fullName!!)

        customerReference?.get()
                ?.addOnSuccessListener(OnSuccessListener<DocumentSnapshot>
                { documentSnapshot -> val customer = documentSnapshot.toObject<Customer>(Customer::class.java!!)
                    val address = customer?.addressNo + ", " + customer?.street + ", " + customer?.city
                    getFullNameTextView.text = "Full Name : $fullName"
                    getAddressTextView.text = "Address : $address"
                    getContactNumberTextView.text = "Contact Number : " + customer?.contactNumber
                })
    }

    private fun getTransactionDetails(db : FirebaseFirestore ?= null, fullName : String ?= null, startDateString : String ?= null, amount : Double ?= null){
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

                            var agreementType : String ?= null
                            agreementType = if(transaction.promissoryNote as Boolean)
                                "Promissory note"
                            else
                                "Land"


                            getAmountTextView.text = "Amount : " + transaction.initialAmount
                            getRemainingAmountTextView.text = "Remaining Amount : " + transaction.remainingAmount.toString()
                            getInterestRateTextView.text = "Interest Rate : " + transaction.interestRate.toString()
                            getMonthlyInterestAmountTextView.text = "Monthly Interest : " + transaction.interestToRecieve.toString()
                            getTotalProfitTextView.text = "Total Profit : " + transaction.totalProfit.toString()
                            getArrearsTextView.text = "Arrears : " + transaction.arrears
                            getStartDateTextView.text = "Start Date : $startDateString"
                            getEndDateTextView.text = "End Date : $endDateString"
                            getTransactionTypeTextView.text = "Transaction Type : $transactionType"
                            getAgreementTypeTextView.text = "Agreement Type : $agreementType"
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
