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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaListViewModel.mediaAll.collect { mediaAll ->
                    binding.mediaGrid.adapter =
                        MediaListAdapter(requireContext(), mediaAll) { mediaId ->
                            findNavController().navigate(
                                HomeFragmentDirections.showMediaDetail(mediaId)
                            )
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