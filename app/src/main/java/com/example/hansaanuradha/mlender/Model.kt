package com.example.hansaanuradha.mlender

import java.util.*

class Customer(val fName : String, val lName: String, val addressNo: String, val street : String, val city : String,
               val state : String, val contactNumber : String)

class Transaction(val from  : String, val startDate : Date, val initialAmount : Double, val endDate : Date,
                  val interestRate : Double, val onlyInterest : Boolean, val completed : Boolean, val remainingAmount : Double,
                  val totalInterest : Double)