package com.datahiveorg.datahive.holder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.WindowCompat
import androidx.core.view.get
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.datahiveorg.datahive.R
import com.datahiveorg.datahive.databinding.ActivityMainNavigationBinding
import com.datahiveorg.datahive.login.LogInActivity
import com.datahiveorg.datahive.login.RegisterActivity
import com.datahiveorg.datahive.termsofservice.PrivacyPolicy
import com.datahiveorg.datahive.termsofservice.TOC
import com.google.android.material.button.MaterialButton
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainNavigation : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavigationBinding

    private lateinit var dataHiveAuth: FirebaseAuth

    //Network Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dataHiveAuth = FirebaseAuth.getInstance()
        Firebase.database.setPersistenceEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        showInAppReview()

        val navigationDrawer = binding.navigationView
        navigationDrawer.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.send_report -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("datahive07@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "DataHive - Report")
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Log.d("Nav drawer intent", "No app found!")
                    }
                }

                R.id.help_and_support -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("datahive07@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "DataHive - Support")
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Log.d("Nav drawer intent", "No app found!")
                    }
                }

                R.id.terms_of_service -> {
                    val intent = Intent(this, TOC::class.java)
                    startActivity(intent)
                }

                R.id.privacy_policy -> {
                    val intent = Intent(this, PrivacyPolicy::class.java)
                    startActivity(intent)
                }

                R.id.log_out -> {
                    dataHiveAuth.signOut()
                    val intent = Intent(this, LogInActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }

                R.id.share -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    val playStoreURL =
                        "https://play.google.com/store/apps/details?id=com.datahiveorg.datahive"
                    intent.putExtra(Intent.EXTRA_TEXT, playStoreURL)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(Intent.createChooser(intent, "Share link using"))

                    } else {
                        Log.d("Nav drawer", "No app found")
                    }
                }
            }
            binding.mainNavDrawerLayout.close()
            true
        }

        val navDrawerHeaderLayout = navigationDrawer.getHeaderView(0)
        val navigationDrawerTitleSubHeader =
            navDrawerHeaderLayout.findViewById<TextView>(R.id.navigation_drawer_hub_header)
        val logInButton =
            navDrawerHeaderLayout.findViewById<MaterialButton>(R.id.nav_drawer_login_redirect)
        val signUpButton =
            navDrawerHeaderLayout.findViewById<MaterialButton>(R.id.nav_drawer_signUpRedirect)
        val navDrawerImage =
            navDrawerHeaderLayout.findViewById<ImageView>(R.id.navigation_drawer_image)
        val logOutMenuItem = navigationDrawer.menu.findItem(R.id.log_out)

        val currentDataHiveUser = dataHiveAuth.currentUser
        currentDataHiveUser?.let {
            if (currentDataHiveUser.isAnonymous) {
                navDrawerImage.visibility = View.GONE
                navigationDrawerTitleSubHeader.visibility = View.GONE
                logOutMenuItem.isVisible = false

            } else {
                logInButton.visibility = View.GONE
                signUpButton.visibility = View.GONE
                navigationDrawerTitleSubHeader.text = dataHiveAuth.currentUser!!.email
            }
        }
        logInButton.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun showNavigationDrawer() {
        binding.mainNavDrawerLayout.open()
    }

    private fun showInAppReview() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d("InAppReview:", "Success: $reviewInfo",)
                }
            } else {
                Toast.makeText(this, "Something went wrong", LENGTH_SHORT).show()
                val reviewErrorCode = (task.exception as ReviewException).errorCode
                Log.d("InAppReview", reviewErrorCode.toString())

            }
        }
    }
}
