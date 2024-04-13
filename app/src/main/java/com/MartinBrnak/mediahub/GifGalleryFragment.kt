package com.MartinBrnak.mediahub

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.MartinBrnak.mediahub.databinding.FragmentGiphyGalleryBinding
import com.MartinBrnak.mediahub.giphyapi.GalleryItem
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
class GifGalleryFragment : Fragment() {
    private var _binding: FragmentGiphyGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val gifGalleryViewModel: GifGalleryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentGiphyGalleryBinding.inflate(inflater, container, false)
        binding.giphyGrid.layoutManager = GridLayoutManager(context, 2)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gifGalleryViewModel.galleryItems.collect { items ->
                    binding.giphyGrid.adapter = GifListAdapter(items, requireContext()) {
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