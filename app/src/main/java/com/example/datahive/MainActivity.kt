package com.example.datahive

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.datahive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


        override fun onCreate(savedInstanceState: Bundle?) {
            try {

                super.onCreate(savedInstanceState)
                binding = ActivityMainBinding.inflate(layoutInflater)
                val view = binding.root
                
                setContentView(view)
            }catch (e:Exception) {
                Log.e(TAG,"MainActivityOnCreateView", e)
                throw e
            }

            supportActionBar?.hide()
        }

}
