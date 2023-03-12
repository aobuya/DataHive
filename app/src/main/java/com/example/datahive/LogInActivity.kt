package com.example.datahive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datahive.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
    }
}