package com.example.datahive.app_usage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.datahive.databinding.AppDataUsageItemBinding

class AppDataAdapter(private var appDataList: List<AppDetails>) :
    RecyclerView.Adapter<AppDataAdapter.AppDataViewHolder>() {

    inner class AppDataViewHolder(private val binding: AppDataUsageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appDetails: AppDetails) {
            binding.appNameTextView.text = appDetails.name
            //binding.appIconImageView.setImageDrawable(appDetails.icon)
            //binding.appDataUsageTextView.text = "${appDetails.dataUsage / 1024 / 1024} MB"
            val dataUsageMB = appDetails.dataUsage / 1024 / 1024.toDouble()

            val dataUsageStr = if (dataUsageMB >= 1024) {
                "%.2f GB".format(dataUsageMB / 1024)
            } else {
                "%.2f MB".format(dataUsageMB)
            }
            binding.appDataUsageTextView.text = dataUsageStr

            val appIconDrawable = appDetails.icon
            val appIconBitmap = Bitmap.createBitmap(
                appIconDrawable.intrinsicWidth,
                appIconDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(appIconBitmap)
            appIconDrawable.setBounds(0, 0, canvas.width, canvas.height)
            appIconDrawable.draw(canvas)

            val scaledIcon = Bitmap.createScaledBitmap(appIconBitmap, 64, 64, false)
            binding.appIconImageView.setImageBitmap(scaledIcon)

        }
    }
    
    fun setFilteredList(filteredList: ArrayList<AppDetails>) {

        this.appDataList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppDataViewHolder {
        val binding =
            AppDataUsageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppDataViewHolder, position: Int) {
        holder.bind(appDataList[position])
    }

    override fun getItemCount() = appDataList.size
}
