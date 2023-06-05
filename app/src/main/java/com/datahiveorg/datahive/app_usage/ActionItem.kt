package com.datahiveorg.datahive.app_usage

import android.content.Context
import android.view.LayoutInflater
import android.view.View

class ActionItem(context: Context, appUsageMenuId : Int) {
    val appUsageMenu : View = LayoutInflater.from(context).inflate(appUsageMenuId,null)

    fun Inflate() {
        //
    }
}