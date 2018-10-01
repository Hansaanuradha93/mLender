package com.example.hansaanuradha.mlender

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import kotlinx.android.synthetic.main.activity_main.*
import android.app.ProgressDialog
import com.example.hansaanuradha.mlender.R.id.emailEditText
import com.example.hansaanuradha.mlender.R.id.passwordEditText
import android.R.attr.password
import kotlin.math.sign


class MainActivity : AppCompatActivity() {

    // FireBase Auth
    private var mAuth: FirebaseAuth? = null

    // Progress Bar
    private var dialog: ProgressDialog? = null

    // GitHub
    // https://github.com/Hansaanuradha93/mLender.git

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        if(currentUser != null) {
            updateUI()
        }

    }

    private fun updateUI() {
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)

    }

    fun next(view: View) {
        signIn()
    }

    fun cancel(view: View){
        emailEditText.text?.clear()
        passwordEditText.text?.clear()

    }

    fun signIn(){
        if(!emailEditText.text?.isEmpty()!! && !passwordEditText.text?.isEmpty()!!){
            // Show Dialog box
            dialog?.setMessage("Sign In, please wait.")
            dialog?.show()

            // Sign In User
            mAuth?.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("result", "signInWithEmail:success")
                        val user = mAuth?.getCurrentUser()
                        // Dismiss Dialog Box
                        dialog?.dismiss()
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("result", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        // Dismiss Dialog Box
                        dialog?.dismiss()
                    }

                    // ...
                }
        }
    }

}
