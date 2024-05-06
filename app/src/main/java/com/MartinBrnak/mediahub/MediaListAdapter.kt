package com.MartinBrnak.mediahub

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MartinBrnak.mediahub.databinding.HomeFragmentBinding
import com.MartinBrnak.mediahub.databinding.MediaItemBinding
import com.bumptech.glide.Glide
import java.util.UUID


class MediaViewHolder(
    private val binding: MediaItemBinding,
    private val context: Context
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
    private val context: Context,
    private val onMediaClicked: (mediaId: UUID) -> Unit
) : RecyclerView.Adapter<MediaViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MediaItemBinding.inflate(inflater, parent, false)
        return MediaViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaAll[position]
        holder.bind(media, onMediaClicked)
    }

    override fun getItemCount() = mediaAll.size
}
