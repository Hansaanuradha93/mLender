package com.example.hansaanuradha.mlender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import android.R.attr.data
import kotlinx.android.synthetic.main.activity_transaction.*
import java.text.SimpleDateFormat


class TransactionActivity : AppCompatActivity() {

    var db : FirebaseFirestore ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        db = FirebaseFirestore.getInstance()
    }

    fun saveTransaction(view : View){

        val dateFormat = SimpleDateFormat("dd/M/yyyy")

        // Customer Details
        val fName = firstNameEditText.text.toString()
        val lName = lastNameEditText.text.toString()
        val addressNumber = addressNoEditText.text.toString()
        val street = streetEditText.text.toString()
        val city = cityEditText.text.toString()
        val state = stateEditText.text.toString()
        val contactNumber = contactNumberEditText.text.toString()

        // Transaction Details
        val startDateString = startDateEditText.text.toString()
        val endDateString = endDateEditText.text.toString()
        val initialAmount = java.lang.Double.parseDouble(initialAmountEditText.text.toString())
        val interestRate = java.lang.Double.parseDouble(interestRateEditText.text.toString())
        val startDate = dateFormat.parse(startDateString)
        val endDate = dateFormat.parse(endDateString)
        var isOnlyInterest = false
        if(onlyInterestRadioButton.isChecked)
            isOnlyInterest = true
        else if(instalmentRadioButton.isChecked)
            isOnlyInterest = false

        val customer : Customer = Customer(fName, lName, addressNumber, street, city, state,
                                            contactNumber)
        val transaction : Transaction = Transaction(customer.fName + " " + customer.lName,startDate, 20000.00, endDate,
                                                    5.0, true, false, 0.00 , 0.00 )

        db?.collection("customers")
                ?.add(customer)
                ?.addOnSuccessListener { documentReference -> Log.d("result", "DocumentSnapshot written with ID: " + documentReference.id) }
                ?.addOnFailureListener { e -> Log.w("result", "Error adding document", e) }

        db?.collection("transactions")
                ?.add(transaction)
                ?.addOnSuccessListener { documentReference -> Log.d("result", "DocumentSnapshot written with ID: " + documentReference.id) }
                ?.addOnFailureListener { e -> Log.w("result", "Error adding document", e) }
    }

}
