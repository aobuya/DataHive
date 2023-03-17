package com.example.datahive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.datahive.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLogInBinding

    private lateinit var dataHiveAuth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

       dataHiveAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.trim().toString().trim()
            val password = binding.etPassword.text.trim().toString().trim()
            val confirmPassword = binding.confirmPassword.text.trim().toString().trim()

            signUpUser(email, password, confirmPassword)
        }
    }

    private fun signUpUser (email : String, password : String, confirmPassword : String) {
        //check for empty fields
        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
            if(password == confirmPassword) {
                dataHiveAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this,"Account created successfully",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this,"Email and passowrd cannot be empty",Toast.LENGTH_SHORT).show()
        }


    }


}