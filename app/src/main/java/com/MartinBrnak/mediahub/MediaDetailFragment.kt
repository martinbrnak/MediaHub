package com.MartinBrnak.mediahub

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.MartinBrnak.mediahub.databinding.MediaDetailFragmentBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.io.File

class MediaDetailFragment : Fragment() {

    private var _binding: MediaDetailFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: MediaDetailFragmentArgs by navArgs()
    private val mediaRepository: MediaRepository by lazy {
        MediaRepository.get()
    }

    private val mediaDetailViewModel: MediaDetailViewModel by viewModels {
        MediaDetailViewModelFactory(args.mediaId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MediaDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                val media = mediaRepository.getMedia(args.mediaId)
                val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val mediaFile = File(downloadDirectory, "${media.id}.${media.format.lowercase()}")
                Glide.with(requireContext())
                    .asGif()
                    .load(mediaFile)
                    .placeholder(R.drawable.profile_icon)
                    .error(R.drawable.magnifying_glass_icon)
                    .into(mediaImageView)

                exportButton.setOnClickListener {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/${media.format.lowercase()}"
                        putExtra(
                            Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                                requireContext(),
                                "${requireContext().packageName}.fileprovider",
                                mediaFile
                            )
                        )
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(Intent.createChooser(shareIntent, "Share ${media.format}"))
                    } else {
                        // No app installed to handle the share intent
                        // Show a toast or handle the case as per your requirements
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}