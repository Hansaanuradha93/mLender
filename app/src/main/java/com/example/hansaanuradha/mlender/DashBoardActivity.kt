package com.example.hansaanuradha.mlender

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dash_board.*

class DashBoardActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        setSupportActionBar(findViewById(R.id.app_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        customerCardView.setOnClickListener {
            val intent = Intent(this, CustomerActivity::class.java)
            startActivity(intent)
        }
        updateCardView.setOnClickListener {
            Toast.makeText(this@DashBoardActivity, "You clicked Update.", Toast.LENGTH_SHORT).show()
        }
        toDoCardView1.setOnClickListener {
            Toast.makeText(this@DashBoardActivity, "You clicked todo 1.", Toast.LENGTH_SHORT).show()
        }
        toDoCardView2.setOnClickListener {
            Toast.makeText(this@DashBoardActivity, "You clicked todo 2.", Toast.LENGTH_SHORT).show()
        }
        addTransactionCardView.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashbboard_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



}
