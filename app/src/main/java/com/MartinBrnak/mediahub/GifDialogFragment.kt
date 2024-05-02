package com.MartinBrnak.mediahub

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.MartinBrnak.mediahub.databinding.DialogPreviewBinding

class GifDialogFragment :DialogFragment() {
    private var _binding: DialogPreviewBinding? =  null
    private val binding get () = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AlertDialog.Builder(requireContext())
            .setMessage("")
            .setPositiveButton("") { _,_ -> }
            .create()
        return super.onCreateDialog(savedInstanceState)
    }
}