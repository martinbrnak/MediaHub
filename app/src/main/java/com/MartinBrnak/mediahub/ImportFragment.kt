package com.MartinBrnak.mediahub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.MartinBrnak.mediahub.databinding.ImportFragmentBinding
import kotlinx.coroutines.launch

class ImportFragment : Fragment() {
    private var _binding: ImportFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImportFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedLink = arguments?.getString("sharedLink")
        if (sharedLink != null) {
            binding.editTextLink.setText(sharedLink)
        }

        binding.buttonImport.setOnClickListener {
            val link = binding.editTextLink.text.toString()
            if (link.isNotEmpty()) {
                downloadMedia(link)
            } else {
                // Show error message or toast indicating empty link
            }
        }
    }

    private fun downloadMedia(link: String) {
        // Check if the link is from TikTok or Instagram
        if (link.contains("tiktok.com") || link.contains("instagram.com")) {
            // TODO: Implement the logic to download the media from the link
            // You can use libraries like OkHttp or Retrofit to make network requests
            // and download the media file
            // Once the media is downloaded, you can save it to the device storage
            // and update the UI accordingly
        } else {
            Toast.makeText(context, "Sorry we haven't implemented downloading from that site yet", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}