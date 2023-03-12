package com.example.datahive.introslider

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragments : ArrayList<Fragment>,
                       fm : FragmentManager,
                       lifecycle : Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    private val introSliderScreens : ArrayList<Fragment> = fragments

    override fun getItemCount(): Int {
        return  introSliderScreens.size
    }

    override fun createFragment(position: Int): Fragment {
        return introSliderScreens[position]
    }
}