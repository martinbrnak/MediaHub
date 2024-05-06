package com.MartinBrnak.mediahub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MartinBrnak.mediahub.databinding.HomeFragmentBinding
import com.bumptech.glide.Glide
import java.util.UUID


class MediaHolder(
    private val binding: HomeFragmentBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(media: Media, onMediaClicked: (mediaId: UUID) -> Unit) {
        /*Glide.with(context)
            .load(media)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.itemImageView)
        */

        binding.root.setOnClickListener {
            onMediaClicked(media.id)
        }


    }
}

class MediaListAdapter(
    private val mediaAll: List<Media>,
    private val onMediaClicked: (mediaId: UUID) -> Unit
) : RecyclerView.Adapter<MediaHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeFragmentBinding.inflate(inflater, parent, false)
        return MediaHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        val media = mediaAll[position]
        holder.bind(media, onMediaClicked)
    }

    override fun getItemCount() = mediaAll.size
}
