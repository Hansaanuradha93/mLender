@file:Suppress("DEPRECATION")

package com.example.hansaanuradha.mlender

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_customer_update.*
import android.support.annotation.NonNull
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference






@Suppress("DEPRECATION")
class CustomerUpdateActivity : AppCompatActivity() {

    // FireStore Reference
    private var db : FirebaseFirestore?= null

    // Progress Bar
    private var dialog: ProgressDialog? = null

    // Fields
    private var fullName : String = ""
    private var fName : String = ""
    private var lName : String = ""
    var addressNumber : String = ""
    var street : String = ""
    var city : String = ""
    var state : String = ""
    var contactNumber : String = ""
    var documentId : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_update)
        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Update Customer"

        // Display Customer Details
        getCustomerDetails()

    }

    private fun getCustomerDetails() {

        // Initialize Views
        initializeViews()

        db = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(this)


        // Show Dialog box
        dialog?.setMessage("loading, please wait.")
        dialog?.show()

        fullName = intent.getStringExtra("fullname")

        val separated = fullName.split(" ")
        fName = separated[0]
        lName = separated[1]

        Log.i("name", fName + " " + fName.length + "\n" + lName + " " + lName.length)

        val customerRef = db?.collection("customers")

        customerRef
                ?.whereEqualTo("fname", fName)
                ?.whereEqualTo("lname", lName)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("result", document.id + " => " + document.data)

                            documentId = document.id
                            addressNumber = document.get("addressNo").toString()
                            street = document.get("street").toString()
                            city= document.get("city").toString()
                            state = document.get("state").toString()
                            contactNumber = document.get("contactNumber").toString()

                            // Display Customer Details
                            updatedFirstNameEditText.setText(fName)
                            updatedLastNameEditText.setText(lName)
                            updatedAddressNoEditText.setText(addressNumber)
                            updatedStreetEditText.setText(street)
                            updatedCityEditText.setText(city)
                            updatedStateEditText.setText(state)
                            updatedContactNumberEditText.setText(contactNumber)

                            // Dismiss Dialog Box
                            dialog?.dismiss()
                        }
                    } else {
                        Log.d("result", "Error getting documents: ", task.exception)
                    }
                }

    }

    fun updateAddress(view : View){

        if (!updatedAddressNoEditText.isEnabled && !updatedStreetEditText.isEnabled && !updatedCityEditText.isEnabled && !updatedStateEditText.isEnabled){
            updatedAddressNoEditText.isEnabled = true
            updatedStreetEditText.isEnabled = true
            updatedCityEditText.isEnabled = true
            updatedStateEditText.isEnabled = true
            updateAddressButton.text = "Update"
        } else {
            if (updatedAddressNoEditText.text.isNullOrEmpty() || updatedStreetEditText.text.isNullOrEmpty()
                    || updatedCityEditText.text.isNullOrEmpty() || updatedStateEditText.text.isNullOrEmpty()){
                Toast.makeText(this, "Please fill the Required Fields", Toast.LENGTH_SHORT).show()
            } else {

                // Show Dialog box
                dialog?.setMessage("Updating Address, please wait.")
                dialog?.show()

                // Update "customer" collection
                val customerRef = db?.collection("customers")?.document(documentId)
                customerRef
                        ?.update("addressNo", updatedAddressNoEditText.text.toString(),
                                "street", updatedStreetEditText.text.toString(),
                                "city", updatedCityEditText.text.toString(),
                                "state", updatedStateEditText.text.toString())
                        ?.addOnSuccessListener(OnSuccessListener<Void> { Log.d("update_result", "DocumentSnapshot successfully updated!")

                            // Disable view
                            updatedAddressNoEditText.isEnabled = false
                            updatedStreetEditText.isEnabled = false
                            updatedCityEditText.isEnabled = false
                            updatedStateEditText.isEnabled = false
                            updateAddressButton.text = "Edit"

                            // Dismiss Dialog Box
                            dialog?.dismiss()
                            Toast.makeText(this, "Updated Address Successfully", Toast.LENGTH_SHORT).show()
                        })
                        ?.addOnFailureListener(OnFailureListener { e -> Log.w("update_result", "Error updating document", e)
                            Toast.makeText(this, "Updated Address Failed", Toast.LENGTH_SHORT).show()
                            // Dismiss Dialog Box
                            dialog?.dismiss()
                        })

                //


            }
        }

    }

    fun updateContactNumber(view : View){
        if (!updatedContactNumberEditText.isEnabled){
            updatedContactNumberEditText.isEnabled = true
            updateContactNumberButton.text = "Update"
        } else {
            // Show Dialog box
            dialog?.setMessage("Updating Contact Number, please wait.")
            dialog?.show()

            if (updatedContactNumberEditText.text.isNullOrEmpty()){
                Toast.makeText(this, "Please fill the Required Fields", Toast.LENGTH_SHORT).show()
            } else {
                // Update "customer" collection
                val customerRef = db?.collection("customers")?.document(documentId)
                customerRef
                        ?.update("contactNumber", updatedContactNumberEditText.text.toString())
                        ?.addOnSuccessListener(OnSuccessListener<Void> { Log.d("update_result", "DocumentSnapshot successfully updated!")

                            // Disable view
                            updatedContactNumberEditText.isEnabled = false
                            updateContactNumberButton.text = "Edit"

                            // Dismiss Dialog Box
                            dialog?.dismiss()
                            Toast.makeText(this, "Updated Contact Number Successfully", Toast.LENGTH_SHORT).show()
                        })
                        ?.addOnFailureListener(OnFailureListener { e -> Log.w("update_result", "Error updating document", e)
                            Toast.makeText(this, "Updated Contact Number Failed", Toast.LENGTH_SHORT).show()
                            // Dismiss Dialog Box
                            dialog?.dismiss()
                        })

                //


            }
        }

    }

    private fun initializeViews(){

        val buttonTextName = "Edit"
        // Disable TextViews
        updatedFirstNameEditText.isEnabled = false
        updatedLastNameEditText.isEnabled = false
        updatedAddressNoEditText.isEnabled = false
        updatedStreetEditText.isEnabled = false
        updatedCityEditText.isEnabled = false
        updatedStateEditText.isEnabled = false
        updatedContactNumberEditText.isEnabled = false

        // Update Button Texts
        updateAddressButton.text = buttonTextName
        updateContactNumberButton.text = buttonTextName

    }































}
