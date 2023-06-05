package com.datahiveorg.datahive.holder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.datahiveorg.datahive.R
import com.datahiveorg.datahive.databinding.ActivityMainNavigationBinding




class MainNavigation : AppCompatActivity() {

   private lateinit var binding: ActivityMainNavigationBinding

   //Network Library

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       WindowCompat.setDecorFitsSystemWindows(window, false)
       binding = ActivityMainNavigationBinding.inflate(layoutInflater)
       val view = binding.root
       setContentView(view)

       val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
       val navController = navHostFragment.navController
       binding.bottomNavigation.setupWithNavController(navController)

   }
}
