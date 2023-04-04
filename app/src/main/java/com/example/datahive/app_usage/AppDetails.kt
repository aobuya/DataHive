package com.example.datahive.app_usage

import android.graphics.drawable.Drawable

class AppDetails(val app: String, val icon: Drawable?, val totalDataUsage: Long) : Comparable<AppDetails> {

    override fun compareTo(other: AppDetails): Int {
        return totalDataUsage.compareTo(other.totalDataUsage)
    }
}


