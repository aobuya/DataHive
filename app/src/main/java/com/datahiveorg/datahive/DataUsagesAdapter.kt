package com.datahiveorg.datahive
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datahiveorg.datahive.databinding.ItemDailyUsageBinding



class DataUsagesAdapter(private val dataUsages: List<UsagesData>) :

    RecyclerView.Adapter<DataUsagesAdapter.DataUsagesViewHolder>() {

    inner class DataUsagesViewHolder(

        private val binding: ItemDailyUsageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UsagesData) {
            binding.apply {
                tvDate.text = item.date
                wifiData.text = item.wifi
                mobileData.text = item.mobile
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataUsagesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = ItemDailyUsageBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return DataUsagesViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: DataUsagesViewHolder, position: Int) {
        val item = dataUsages[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataUsages.size
}