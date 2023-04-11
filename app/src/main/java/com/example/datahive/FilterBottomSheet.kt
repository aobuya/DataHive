package com.example.datahive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.datahive.databinding.BottomConfigBinding
import com.example.datahive.databinding.FilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FilterBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: FilterBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // access views using binding

    }
}
