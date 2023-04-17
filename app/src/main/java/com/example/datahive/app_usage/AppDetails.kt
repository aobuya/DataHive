package com.example.datahive.app_usage

import android.graphics.drawable.Drawable
import java.util.Date

class AppDetails(val app: String, var icon: Drawable?, val totalDataUsage: Long, val date: String) : Comparable<AppDetails> {

    override fun compareTo(other: AppDetails): Int {
        return totalDataUsage.compareTo(other.totalDataUsage)
    }
}


