@file:Suppress("DEPRECATION")

package com.example.hansaanuradha.mlender

import android.app.ProgressDialog
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.hansaanuradha.mlender.R.id.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_transaction_update.*
import java.text.SimpleDateFormat
import java.util.*


class TransactionUpdateActivity : AppCompatActivity() {

    // Progress Bar
    private var dialog: ProgressDialog? = null
    // FireStore Reference
    private var db : FirebaseFirestore?= null

    // Fields
    private var fullName : String ?= null
    private var startDateString : String ?= null
    private var amount : Double ?= null
    private var amountEntered : Double ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_update)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "UPDATE TRANSACTION"

        dialog = ProgressDialog(this)

        db = FirebaseFirestore.getInstance()

        fullName = intent.getStringExtra("fullname")
        startDateString = intent.getStringExtra("startDateString")
        amount = intent.getDoubleExtra("amount", 0.0)


        getArrears()

    }

    private fun getArrears(){
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val startDate = dateFormat.parse(startDateString)
        db?.collection("transactions")
                ?.whereEqualTo("from", fullName)
                ?.whereEqualTo("initialAmount", amount)
                ?.whereEqualTo("startDate", startDate)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("result", document.id + " => " + document.data)

                            val currentArrears : Double= java.lang.Double.parseDouble(document.get("arrears").toString())

                            if (currentArrears > 0.0){

                                currentArrearsTextView.isEnabled = true
                                currentArrearsTextView.visibility = View.VISIBLE
                                currentArrearsTextView.setTextColor(Color.rgb(255,0,0))
                                currentArrearsTextView.text = "There is $currentArrears amount of Arrears"
                            } else {

                                currentArrearsTextView.isEnabled = false
                                currentArrearsTextView.visibility = View.GONE
                            }


                        }
                    } else {
                        Log.d("result", "Error getting documents: ", task.exception)
                    }
                }

    }

    fun updateTransaction(view : View){

        if(!paidInterestEditText.text.isNullOrEmpty() && !paidAmountEditText.text.isNullOrEmpty()){
            if (paidAmountEditText.text.isNullOrEmpty()){
                amountEntered = 0.0
            }

            // Update
            updateFields()
        } else
            Toast.makeText(this, "Please Fill the Required Fields $amountEntered", Toast.LENGTH_SHORT).show()
    }

    private fun updateFields(){
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val startDate = dateFormat.parse(startDateString)
        // Show Dialog box
        dialog?.setMessage("Updating Transaction, please wait.")
        dialog?.show()

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
                                val paidAmount: Double = java.lang.Double.parseDouble(paidAmountEditText.text.toString())
                                val currentRemainingAmount: Double = java.lang.Double.parseDouble(document.get("remainingAmount").toString())
                                if (paidAmount > currentRemainingAmount) {
                                    Toast.makeText(this, "Invalid Input for Amount Paid", Toast.LENGTH_SHORT).show()
                                    // Dismiss Dialog Box
                                    dialog?.dismiss()
                                } else {
                                    val updatedRemainingAmount: Double = currentRemainingAmount - paidAmount

                                    val currentTotalProfit: Double = java.lang.Double.parseDouble(document.get("totalProfit").toString())
                                    val paidInterest: Double = java.lang.Double.parseDouble(paidInterestEditText.text.toString())
                                    val updatedTotalProfit: Double = currentTotalProfit + paidInterest

                                    val currentInterestRate: Double = java.lang.Double.parseDouble(document.get("interestRate").toString())
                                    val updatedInterestToReceive: Double = updatedRemainingAmount * currentInterestRate / 100

                                    var updatedStatus = !notCompletedRadioButton.isChecked
                                    updatedStatus = completedRadioButton.isChecked

                                    var currentInterestToReceive: Double = java.lang.Double.parseDouble(document.get("interestToRecieve").toString())
                                    var arrears: Double = java.lang.Double.parseDouble(document.get("arrears").toString())

                                    if (currentRemainingAmount == 0.0 && updatedInterestToReceive == 0.0 && arrears == 0.0) {
                                        Toast.makeText(this, "This Transaction is Completed", Toast.LENGTH_SHORT).show()
                                        // Dismiss Dialog Box
                                        dialog?.dismiss()
                                    } else {
                                        if(paidInterest > currentInterestToReceive + arrears){
                                            Toast.makeText(this, "Invalid input for Interest Paid", Toast.LENGTH_SHORT).show()
                                            // Dismiss Dialog Box
                                            dialog?.dismiss()
                                        } else {

                                            // Get the Interest Gap
                                            var interestGap: Double = currentInterestToReceive - paidInterest
                                            arrears += interestGap

                                            if (updatedRemainingAmount == 0.0 && updatedInterestToReceive == 0.0 && arrears <= 0.0) {
                                                // Update Transaction Status to Completed
                                                updatedStatus = true
                                                updateTransactionButton.isEnabled = false
                                            }

                                            Log.i("result", "Previous :$currentRemainingAmount\n Remain : $updatedRemainingAmount")
                                            transactionRef
                                                    ?.update("remainingAmount", updatedRemainingAmount,
                                                            "totalProfit", updatedTotalProfit,
                                                            "interestToRecieve", updatedInterestToReceive,
                                                            "completed", updatedStatus,
                                                            "arrears", arrears)
                                                    ?.addOnSuccessListener(OnSuccessListener<Void> {
                                                        Log.d("result", "DocumentSnapshot successfully updated!")
                                                        Toast.makeText(this, "Transaction Successfully Updated", Toast.LENGTH_SHORT).show()
                                                        // Get Arrears
                                                        getArrears()
                                                        // Clear Text
                                                        clearText()
                                                        if (updatedRemainingAmount == 0.0 && updatedInterestToReceive == 0.0 && arrears == 0.0) {
                                                            Toast.makeText(this, "This Transaction is Completed", Toast.LENGTH_SHORT).show()
                                                            // Clear Text
                                                            clearText()
                                                        }
                                                        // Dismiss Dialog Box
                                                        dialog?.dismiss()
                                                    })
                                                    ?.addOnFailureListener(OnFailureListener { e ->
                                                        Log.w("result", "Error updating document", e)
                                                        Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()
                                                        // Dismiss Dialog Box
                                                        dialog?.dismiss()

                                                    })
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("result", "Error getting documents: ", task.exception)
                        }
                    }


        }
    }

    private fun clearText() {
        paidAmountEditText.setText("")
        paidInterestEditText.setText("")
    }
}
