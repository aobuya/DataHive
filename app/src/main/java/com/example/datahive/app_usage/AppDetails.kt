package com.example.datahive.app_usage

import android.graphics.drawable.Drawable

data class AppDetails(
    val name: String,
    val icon: Drawable?,
    val dataUsage: Long
)

