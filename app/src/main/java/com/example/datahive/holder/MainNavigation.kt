package com.example.datahive.holder

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datahive.DataUsagesAdapter
import com.example.datahive.R
import com.example.datahive.UsagesData
import com.example.datahive.databinding.ActivityMainNavigationBinding
import dev.jahidhasanco.networkusage.*
import java.util.logging.Handler

class MainNavigation : AppCompatActivity() {

   private lateinit var binding: ActivityMainNavigationBinding

   //Network Library
   private lateinit var dataUsagesAdapter: DataUsagesAdapter
   private var usagesDataList = ArrayList<UsagesData>()
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
