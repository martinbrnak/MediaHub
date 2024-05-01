package com.MartinBrnak.mediahub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.MartinBrnak.mediahub.R
import com.MartinBrnak.mediahub.databinding.PreviewOverlayBinding
import com.bumptech.glide.Glide

class PreviewFragment : Fragment() {

    private var _binding: PreviewOverlayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PreviewOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: PreviewFragmentArgs by navArgs()
        val imageUrl = args.gifUrl
        val imageView = view.findViewById<ImageView>(R.id.previewImageView)

        // Load the GIF using the imageUrl
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // You can add additional methods here to update the content of the preview fragment
}