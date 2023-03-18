package com.example.datahive.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.datahive.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {
    private lateinit var binding : ActivityResetPasswordBinding

    private lateinit var dataHiveAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        binding.btnResetPassword.setOnClickListener{

            //resetPassword()
            Toast.makeText(this,"Password sent, check your inbox",Toast.LENGTH_SHORT).show()
        }

        //redirect users to the login screen
        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }
    //Firebase password rest implementation
    private fun resetPassword() {
        TODO("Not yet implemented")
    }
}