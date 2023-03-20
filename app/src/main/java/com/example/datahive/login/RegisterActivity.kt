package com.example.datahive.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.datahive.databinding.ActivityRegisterBinding
import com.example.datahive.holder.MainNavigation
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding

    private lateinit var dataHiveAuth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        //login if already registered
        binding.loginRedirect.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser (email : String, password : String, confirmPassword : String) {
        //check for empty fields
        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
            if(password == confirmPassword) {
                dataHiveAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this,"Account created successfully",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainNavigation::class.java)
                        startActivity(intent)

                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this,"Email and password cannot be empty",Toast.LENGTH_SHORT).show()
        }


    }


}