package com.MartinBrnak.mediahub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.MartinBrnak.mediahub.databinding.FragmentGiphyGalleryBinding
import com.MartinBrnak.mediahub.databinding.HomeFragmentBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "HomePageFragment"
class HomeFragment : Fragment() {
    private var _binding : HomeFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val mediaListViewModel: MediaListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.mediaGrid.layoutManager = GridLayoutManager(context, 2)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaRepository = MediaRepository.get()
        val mediaListAdapter = MediaListAdapter(requireContext(), emptyList()) {}

        binding.mediaGrid.layoutManager = GridLayoutManager(context, 2)
        binding.mediaGrid.adapter = mediaListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaRepository.getMediaAll().collect { mediaList ->
                    mediaListAdapter.updateMediaList(mediaList)

                    if (mediaListAdapter.itemCount == 0) {
                        binding.mediaGrid.visibility = View.GONE
                        binding.welcomeMessage.visibility = View.VISIBLE
                    } else {
                        binding.mediaGrid.visibility = View.VISIBLE
                        binding.welcomeMessage.visibility = View.GONE
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