package com.example.hansaanuradha.mlender

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_customer_update.*


class CustomerUpdateActivity : AppCompatActivity() {

    // FireStore Reference
    private var db : FirebaseFirestore?= null
    // Fields
    var fullName : String = ""
    var fName : String = ""
    var lName : String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_update)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Update Customer"

        db = FirebaseFirestore.getInstance()

        fullName = intent.getStringExtra("fullname")

        val separated = fullName.split(" ")
        fName = separated[0]
        lName = separated[1]

        Log.i("name", fName + " " + fName.length + "\n" + lName + " " + lName.length)

        val customerRef = db?.collection("customers")?.document(fullName)
        customerRef?.get()?.addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    Log.d("result", "DocumentSnapshot data: " + document.data!!)
                    updatedFirstNameEditText.setText(fName)
                    updatedLastNameEditText.setText(lName)
                } else {
                    Log.d("result", "No such document")
                }
            } else {
                Log.d("result", "get failed with ", task.exception)
            }
        })

    }
}
