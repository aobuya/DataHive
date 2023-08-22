package com.datahiveorg.datahive.termsofservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.WindowCompat
import com.datahiveorg.datahive.databinding.ActivityPrivacyPolicyBinding



class PrivacyPolicy : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    binding.progressBar.visibility = ProgressBar.GONE
                } else {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                    binding.progressBar.progress = newProgress
                }
            }
        }
        binding.webView.loadUrl("https://datahive-b0d2a.web.app/toc.html")
        binding.webView.webViewClient = WebViewClient()
    }
}