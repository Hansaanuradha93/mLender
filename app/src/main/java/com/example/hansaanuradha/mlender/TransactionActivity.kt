@file:Suppress("DEPRECATION")

package com.example.hansaanuradha.mlender

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import android.app.ProgressDialog
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_transaction.*
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NAME_SHADOWING")
class TransactionActivity : AppCompatActivity() {

    // Firestore Reference
    var db : FirebaseFirestore ?= null
    var customerReference : DocumentReference ?= null
    var transactionRef : CollectionReference ?= null

    // Progress Bar
    private var dialog: ProgressDialog? = null

    // Fields
    var isStartDate : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Transaction"

        db = FirebaseFirestore.getInstance()

        dialog = ProgressDialog(this)


    }

    fun saveTransaction(view : View){

        validateAndSave()
    }

    private fun validateAndSave(){
        if (!firstNameEditText.text.isNullOrEmpty() && !lastNameEditText.text.isNullOrEmpty()
                && !addressNoEditText.text.isNullOrEmpty() && !streetEditText.text.isNullOrEmpty()
                && !cityEditText.text.isNullOrEmpty() && !stateEditText.text.isNullOrEmpty()
                && !contactNumberEditText.text.isNullOrEmpty()
                && !initialAmountEditText.text.isNullOrEmpty() && !startDateEditText.text.isNullOrEmpty()
                && !endDateEditText.text.isNullOrEmpty() && !interestRateEditText.text.isNullOrEmpty() ){




            val startDateString = startDateEditText.text.toString()
            val endDateString = endDateEditText.text.toString()
            val dateFormat = SimpleDateFormat("dd/M/yyyy")
            val startDate = dateFormat.parse(startDateString)
            val endDate = dateFormat.parse(endDateString)

            val minimumInitialAmount = 4000
            val minimumContactNumberLength = 10

            if(contactNumberInputLayout.editText!!.length() < minimumContactNumberLength)
                    Toast.makeText(this@TransactionActivity, "Enter a valid Contact Number", Toast.LENGTH_SHORT).show()
            else if(java.lang.Double.parseDouble(initialAmountEditText.text.toString()) < minimumInitialAmount)
                Toast.makeText(this@TransactionActivity, "Enter a valid Amount", Toast.LENGTH_SHORT).show()
            else if (startDate >= endDate)
                Toast.makeText(this@TransactionActivity, "Invalid Dates", Toast.LENGTH_SHORT).show()
            else {
                    // Show Dialog box
                    dialog?.setMessage("Saving Transaction, please wait.")
                    dialog?.show()

                    val fName = firstNameEditText.text.toString()
                    val lName = lastNameEditText.text.toString()

                    // Save Customer to Db
                    addCustomer(fName, lName)

                    // Save Transaction to Db
                    addTransaction(fName, lName)
                }


        } else
            Toast.makeText(this@TransactionActivity, "Please Fill all the information", Toast.LENGTH_SHORT).show()
    }

    private fun addCustomer(fName : String, lName : String){

        // Customer Details
        val fullName = "$fName $lName"
        val addressNumber = addressNoEditText.text.toString()
        val street = streetEditText.text.toString()
        val city = cityEditText.text.toString()
        val state = stateEditText.text.toString()
        val contactNumber = contactNumberEditText.text.toString()


        val customer : Customer = Customer(fName, lName, addressNumber, street, city, state,
                contactNumber)

        customerReference = db?.collection("customers")?.document(fullName)

        // Add Customer to db
        customerReference
                ?.set(customer)
                ?.addOnSuccessListener { Log.d("result", "DocumentSnapshot written with ID: ") }
                ?.addOnFailureListener { e -> Log.w("result", "Error adding document", e)
                    // Dismiss Dialog Box
                    dialog?.dismiss()
                    Toast.makeText(this@TransactionActivity, "Transaction Failed !!", Toast.LENGTH_LONG).show()
                }

    }

    private fun addTransaction(fName : String, lName : String){

        // Transaction Details
        val fullName = "$fName $lName"
        val startDateString = startDateEditText.text.toString()
        val endDateString = endDateEditText.text.toString()
        val dateFormat = SimpleDateFormat("dd/M/yyyy")
        val startDate = dateFormat.parse(startDateString)
        val endDate = dateFormat.parse(endDateString)
        val initialAmount = java.lang.Double.parseDouble(initialAmountEditText.text.toString())
        val interestRate = java.lang.Double.parseDouble(interestRateEditText.text.toString())
        var isOnlyInterest = false
        if(onlyInterestRadioButton.isChecked)
            isOnlyInterest = true
        else if(instalmentRadioButton.isChecked)
            isOnlyInterest = false
        var promissoryNote = true
        if(promissoryNoteRadioButton.isChecked)
            promissoryNote = true
        else if(landRadioButton.isChecked)
            promissoryNote = false
        val isCompleted = false
        val interestToReceive = initialAmount * (interestRate / 100)
        val totalProfit = 0.00
        val arrears = 0.0


        val transaction : Transaction = Transaction(fullName,startDate, initialAmount, endDate,
                interestRate, isOnlyInterest, isCompleted, initialAmount, interestToReceive, totalProfit, arrears, promissoryNote)

        transactionRef = db?.collection("transactions")


        // Add Transaction to db
        transactionRef
                ?.add(transaction)
                ?.addOnSuccessListener { documentReference -> Log.d("result", "DocumentSnapshot written with ID: " + documentReference.id)
                    // Dismiss Dialog Box
                    dialog?.dismiss()
                    Toast.makeText(this@TransactionActivity, "Transaction Saved", Toast.LENGTH_LONG).show()
                    clearText()
                }
                ?.addOnFailureListener { e -> Log.w("result", "Error adding document", e)
                    // Dismiss Dialog Box
                    dialog?.dismiss()
                    Toast.makeText(this@TransactionActivity, "Transaction Failed !!", Toast.LENGTH_LONG).show()
                }
    }

    private fun clearText() {
        initialAmountEditText.setText("")
        startDateEditText.setText("")
        endDateEditText.setText("")
        interestRateEditText.setText("")

    }

    fun getStartDate(view : View){
        isStartDate = true
        Log.i("result", isStartDate.toString())

        // Get Date
        getCalendarValue(isStartDate)
    }

    fun getEndDate(view : View){
        isStartDate = false
        Log.i("result", isStartDate.toString())

        // Get Date
        getCalendarValue(isStartDate)

    }

    private fun getCalendarValue(isStartDate : Boolean){

        val calendar = Calendar.getInstance()
        val date : Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month : Int = calendar.get(Calendar.MONTH)
        val year : Int = calendar.get(Calendar.YEAR)

        val dateTimePicker : DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, i1, i2, i3 ->

            val month = i2 + 1
            val dateString = "$i3/$month/$i1"
            if (isStartDate)
                startDateEditText.setText(dateString)
            else
                endDateEditText.setText(dateString)
        }, year,month,date)

        dateTimePicker.show()

    }




}
