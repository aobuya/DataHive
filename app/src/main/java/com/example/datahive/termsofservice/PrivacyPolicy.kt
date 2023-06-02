package com.example.datahive.termsofservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.example.datahive.R
import com.example.datahive.databinding.ActivityPrivacyPolicyBinding
import com.example.datahive.databinding.ActivityTocBinding

class PrivacyPolicy : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding.webView.loadUrl("https://docs.google.com/document/d/e/2PACX-1vRI8RxsVvMPgSwZNjOgLP-SqX8GSOEVns4tqN4YQjeWsHzPuU48TKwihhncudN8VZ3TDHlqypUlFpCM/pub")
        binding.webView.webViewClient = WebViewClient()
    }
}