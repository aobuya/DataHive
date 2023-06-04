package com.example.datahive.login

import android.app.Activity
import android.content.ContentProviderClient
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.example.datahive.R
import com.example.datahive.databinding.ActivityRegisterBinding
import com.example.datahive.holder.MainNavigation
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var dataHiveAuth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //configure google sign in
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.datahive_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()


        dataHiveAuth = FirebaseAuth.getInstance()



        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.trim().toString().trim()
            val password = binding.etPassword.text.trim().toString().trim()
            val confirmPassword = binding.confirmPassword.text.trim().toString().trim()

            //check user type
            if(dataHiveAuth.currentUser!!.isAnonymous) {
                signUpAnonymousUser(email, password, confirmPassword)
            }else{
            signUpUser(email, password, confirmPassword)
            }
        }
        //login if already registered
        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        //log in with google
        binding.btnGoogleSignUp.setOnClickListener {
            signUpUserWithGoogle()
        }
    }

    private fun signUpUser(email: String, password: String, confirmPassword: String) {
        //check for empty fields
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                dataHiveAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dataHiveAuth.signInWithEmailAndPassword(email, password)
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainNavigation::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }


    }

    private fun signUpAnonymousUser(email: String, password: String, confirmPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                val credential = EmailAuthProvider.getCredential(email, password)

                dataHiveAuth.currentUser!!.linkWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "linkWithCredential:success")
                            Toast.makeText(this, "Account created successfully",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainNavigation::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.exception)
                            Toast.makeText(
                                this,
                                task.exception.toString(),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUpUserWithGoogle() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = googleCredential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            dataHiveAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = dataHiveAuth.currentUser
                                        val intent = Intent(this, MainNavigation::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                        Toast.makeText(
                                            this,
                                            "Error! please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.d("REGISTER WITH GOOGLE API EXCEPTION", "API Exception Error", e)
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                    }
                }
            }

        }

    }
}