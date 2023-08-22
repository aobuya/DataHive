package com.datahiveorg.datahive.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.datahiveorg.datahive.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ResetPassword : AppCompatActivity() {
    private lateinit var binding : ActivityResetPasswordBinding

    private lateinit var dataHiveAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dataHiveAuth = FirebaseAuth.getInstance()
        Firebase.database.setPersistenceEnabled(true)

        binding.btnResetPassword.setOnClickListener{
            val email = binding.enterEmail.text.trim().toString().trim()

            resetPassword(email)
        }

        //redirect users to the login screen
        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    //Firebase password rest implementation
    private fun resetPassword(email: String) {

        if (email.isNotEmpty()) {
            dataHiveAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent")
                    Toast.makeText(
                        this,
                        "Password sent, check your email inbox",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account does not exist", Toast.LENGTH_LONG).show()
                }
            }
        }else {
            Toast.makeText(this, "Please enter you email", Toast.LENGTH_SHORT).show()
        }
    }
}