package com.MartinBrnak.mediahub

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.MartinBrnak.mediahub.databinding.FragmentGiphyGalleryBinding
import com.MartinBrnak.mediahub.GifGalleryFragmentDirections
import kotlinx.coroutines.launch
class GifGalleryFragment : Fragment() {
    private var _binding: FragmentGiphyGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val gifGalleryViewModel: GifGalleryViewModel by viewModels()
    private lateinit var searchBar: EditText


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
        // Bind the search bar view
        searchBar = view.findViewById(R.id.search_bar)

        // Set up the search bar
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchBar.text.toString().trim()
                if (query.isNotEmpty()) {
                    gifGalleryViewModel.searchGifs(query)
                }
                true
            } else {
                false
            }
        }

        // Set up a text change listener on the search bar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.toString()?.trim()?.isEmpty() == true) {
                    // If the search bar text becomes empty, fetch trending GIFs
                    gifGalleryViewModel.fetchTrendingGifs()
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gifGalleryViewModel.galleryItems.collect { items ->
                    val gifListAdapter = GifListAdapter(items, requireContext()) { clickedItem ->
                        findNavController().navigate(
                            GifGalleryFragmentDirections.showGifDetail(clickedItem.images.original.url) // Pass URL as argument
                        )
                    }
                    binding.giphyGrid.adapter = gifListAdapter
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}