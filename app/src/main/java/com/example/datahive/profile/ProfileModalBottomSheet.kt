package com.example.datahive.profile

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.datahive.databinding.FragmentProfileModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentProfileModalBottomSheetBinding

    private lateinit var dataHiveUserViewModel : UserViewModel
    
    private var dataWriteListener: WriteToRoomDBListener? = null

    interface WriteToRoomDBListener {

        fun onDataWritten(data: User)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileModalBottomSheetBinding.inflate(inflater, container, false)

        dataHiveUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.profileBottomsheetButton.setOnClickListener {
            dismiss()

        }

        return binding.root
    }

    companion object {
        const val TAG = "ProfileModalBottomSheet"
    }

    private fun insertToRoomDB(username: String) {
        if (inputCheck(username)) {
            val user = User(0, username)
            try {
                dataHiveUserViewModel.addUser(user)
                dataWriteListener?.onDataWritten(user)
                Toast.makeText(requireContext(), "Changes have been saved", Toast.LENGTH_SHORT)
                    .show()

            } catch (e: Exception) {
                Log.d("Add to ROOM DB", "${e.printStackTrace()}")
                Toast.makeText(
                    requireContext(), "Something went wrong, please try again.", Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun inputCheck(username: String): Boolean {
        return username.isNotEmpty()
    }
     fun setDataWriteListener(listener: WriteToRoomDBListener) {
        dataWriteListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        insertToRoomDB(username = "${binding.profileUsername.text}")

    }
}