package com.example.hansaanuradha.mlender

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_transaction_update.*
import java.text.SimpleDateFormat
import java.util.*


class TransactionUpdateActivity : AppCompatActivity() {

    // FireStore Reference
    var db : FirebaseFirestore?= null

    // Fields
    var fullName : String ?= null
    var startDateString : String ?= null
    var amount : Double ?= null
    var amountEntered : Double ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_update)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "UPDATE TRANSACTION"

        db = FirebaseFirestore.getInstance()

        fullName = intent.getStringExtra("fullname")
        startDateString = intent.getStringExtra("startDateString")
        amount = intent.getDoubleExtra("amount", 0.0)




    }

    fun updateTransaction(view : View){
        if(!paidInterestEditText.text.isNullOrEmpty()){
            if (paidAmountEditText.text.isNullOrEmpty()){
                amountEntered = 0.0
            }

            // Update
            updateFields()
        } else
            Toast.makeText(this, "Please Fill the Required Fields " + amountEntered, Toast.LENGTH_SHORT).show()
    }

    fun updateFields(){
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val startDate = dateFormat.parse(startDateString)

        if (amount != 0.0) {
            db?.collection("transactions")
                    ?.whereEqualTo("from", fullName)
                    ?.whereEqualTo("initialAmount", amount)
                    ?.whereEqualTo("startDate", startDate)
                    ?.get()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                Log.d("result", document.id + " => " + document.data)

                                val transactionRef = db?.collection("transactions")?.document(document.id)
                                val paidAmount : Double = java.lang.Double.parseDouble(paidAmountEditText.text.toString())
                                val currentRemainingAmount : Double =  java.lang.Double.parseDouble(document.get("remainingAmount").toString())
                                if (paidAmount > currentRemainingAmount)
                                    Toast.makeText(this, "Invalid Input for Paid Amount", Toast.LENGTH_SHORT).show()
                                val updatedRemainingAmount : Double = currentRemainingAmount - paidAmount

                                val currentTotalProfit : Double = java.lang.Double.parseDouble(document.get("totalProfit").toString())
                                val paidInterest : Double = java.lang.Double.parseDouble(paidInterestEditText.text.toString())
                                val updatedTotalProfit : Double = currentTotalProfit + paidInterest

                                val currentInterestRate : Double = java.lang.Double.parseDouble(document.get("interestRate").toString())
                                val currentInterestToReceive : Double = updatedRemainingAmount * currentInterestRate / 100

                                var updatedStatus = !notCompletedRadioButton.isChecked
                                updatedStatus = completedRadioButton.isChecked


                                Log.i("result", "Previous :" + currentRemainingAmount + "\n Remain : " + updatedRemainingAmount)
                                transactionRef
                                        ?.update("remainingAmount", updatedRemainingAmount,
                                                "totalProfit", updatedTotalProfit,
                                                 "interestToRecieve", currentInterestToReceive,
                                                 "completed", updatedStatus)
                                        ?.addOnSuccessListener(OnSuccessListener<Void> { Log.d("result", "DocumentSnapshot successfully updated!") })
                                        ?.addOnFailureListener(OnFailureListener { e -> Log.w("result", "Error updating document", e) })
                            }
                        } else {
                            Log.d("result", "Error getting documents: ", task.exception)
                        }
                    }


        }
    }
}
