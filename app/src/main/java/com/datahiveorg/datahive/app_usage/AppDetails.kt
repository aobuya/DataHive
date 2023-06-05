package com.datahiveorg.datahive.app_usage

import android.graphics.drawable.Drawable

class AppDetails(val app: String, var icon: Drawable? = null, val totalDataUsage: Long, val date: String? = null) : Comparable<AppDetails> {

    override fun compareTo(other: AppDetails): Int {
        return totalDataUsage.compareTo(other.totalDataUsage)
    }
}


