package com.example.datahive.introslider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.datahive.R
import com.example.datahive.databinding.FragmentViewPagerBinding
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator


class ViewPager : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val introSliderScreens = arrayListOf<Fragment>(
            FirstScreen(), SecondScreen(), ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            introSliderScreens, requireActivity().supportFragmentManager, lifecycle
        )
        val wormDotsIndicator: WormDotsIndicator = binding.wormDotsIndicator
        val viewPager2: ViewPager2 = binding.viewPager2

        viewPager2.adapter = adapter
        wormDotsIndicator.attachTo(viewPager2)

        /*TODO
            Change dot indicator colors based on current screen

        when (viewPager2.currentItem) {
            0 -> {
                wormDotsIndicator.setDotIndicatorColor(R.color.black)
                wormDotsIndicator.setStrokeDotsIndicatorColor(R.color.black)
            }

            1 -> {
                wormDotsIndicator.setDotIndicatorColor(R.color.bee_yellow)
                wormDotsIndicator.setStrokeDotsIndicatorColor(R.color.bee_yellow)
            }
            2 -> {
                wormDotsIndicator.setDotIndicatorColor(R.color.black)
                wormDotsIndicator.setStrokeDotsIndicatorColor(R.color.bee_yellow)
            }

        }
         */

        return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}