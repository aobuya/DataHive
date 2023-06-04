package com.example.datahive.introslider

import android.app.AppOpsManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.datahive.R
import com.example.datahive.databinding.FragmentLobbyBinding
import com.example.datahive.holder.MainNavigation
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataHiveAuth: FirebaseAuth

    private var usageStatsPermissionDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        dataHiveAuth = FirebaseAuth.getInstance()


        binding.getStartedButton.setOnClickListener {
            dataHiveAuth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

            requireActivity().run {
                startActivity(Intent(this, MainNavigation::class.java))
                finish()
            }
        }
        binding.registerRedirect.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }

        return binding.root
    }

    private fun showUsageStatsPermissionDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_permission_explanation, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
        usageStatsPermissionDialog = builder.create()

        val btnGrantPermission = dialogView.findViewById<Button>(R.id.btnGrantPermission)
        btnGrantPermission.isEnabled = false

        val acceptCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_accept)
        acceptCheckBox.setOnCheckedChangeListener { _, isChecked ->
            btnGrantPermission.isEnabled = isChecked
        }
        btnGrantPermission.setOnClickListener {
            requestUsageStatsPermission()
            usageStatsPermissionDialog?.dismiss()
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            usageStatsPermissionDialog?.dismiss()
            requireActivity().finishAffinity()

        }

        //make policies and TOC clickable
        val tocDisclaimer = dialogView.findViewById<TextView>(R.id.disclaimer)
        val tocDisclaimerText = getString(R.string.accept_toc)

        val clickableWords = mapOf("Policies" to "https://docs.google.com/document/d/e/2PACX-1vRI8RxsVvMPgSwZNjOgLP-SqX8GSOEVns4tqN4YQjeWsHzPuU48TKwihhncudN8VZ3TDHlqypUlFpCM/pub", 
            "Terms and Conditions" to "https://docs.google.com/document/d/e/2PACX-1vSlKXV0YEAjKjj41HJBGbZEgwNgHWosibhwRjOtzvFff6a3caJ1M0EYTtdxUTUCk42_P0eOSwf_LUHX/pub?embedded=true")

        val spannableString = SpannableString(tocDisclaimerText)

        for ((clickableWord, url) in clickableWords) {
            val startIndex = tocDisclaimerText.indexOf(clickableWord)
            val endIndex = startIndex + clickableWord.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            }

            spannableString.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tocDisclaimer.text = spannableString
        tocDisclaimer.movementMethod = LinkMovementMethod.getInstance()

        usageStatsPermissionDialog?.show()
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager =
            requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            requireContext().packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display permission explanation dialog if permission is not granted
        if (!hasUsageStatsPermission()) {
            showUsageStatsPermissionDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        usageStatsPermissionDialog?.dismiss()
    }

}