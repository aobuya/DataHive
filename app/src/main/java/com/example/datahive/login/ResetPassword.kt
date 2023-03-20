package com.example.datahive.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.datahive.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ResetPassword : AppCompatActivity() {
    private lateinit var binding : ActivityResetPasswordBinding

    private lateinit var dataHiveAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dataHiveAuth = FirebaseAuth.getInstance()
        val getUser = dataHiveAuth.currentUser



        binding.btnResetPassword.setOnClickListener{
            val email = binding.enterEmail.text.trim().toString().trim()

            resetPassword(email, getUser)
        }

        //redirect users to the login screen
        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    //Firebase password rest implementation
    private fun resetPassword(email: String, user: FirebaseUser?) {

        if (email.isNotEmpty()) {
            dataHiveAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent")
                    Toast.makeText(
                        this,
                        "Password sent, check your email inbox",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this, SignUpActivity::class.java)
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