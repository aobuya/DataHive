package com.example.datahive.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.datahive.databinding.ActivityLogInBinding
import com.example.datahive.holder.MainNavigation
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    private lateinit var dataHiveAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataHiveAuth = FirebaseAuth.getInstance()

        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {
            singInUser()
        }
        //reset Password
        binding.forgotPass.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
            finish()
        }

        //register in redirect
        binding.registerRedirect.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun singInUser() {

        val email = binding.etEmail.text.trim().toString().trim()
        val password = binding.etPassword.text.trim().toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            dataHiveAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        val getUser = dataHiveAuth.currentUser
                        Toast.makeText(this,"Login  successfully",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainNavigation::class.java)
                        startActivity(intent)
                        finish()
                        // ...
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }


        }else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}