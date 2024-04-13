package com.MartinBrnak.mediahub

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.MartinBrnak.mediahub.giphyapi.GalleryItem
import com.MartinBrnak.mediahub.databinding.ListItemGalleryBinding
import com.bumptech.glide.Glide

class GifViewHolder(
    private val binding: ListItemGalleryBinding,
    private val context: Context,
    private val onLongClickListener: (position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init{
        binding.root.setOnLongClickListener {
            onLongClickListener(adapterPosition)
            true
        }
    }
    fun bind(galleryItem: GalleryItem) {
        Glide.with(context)
            .load(galleryItem.images.original.url)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.itemImageView)
    }
}

class GifListAdapter(
    private val galleryItems: List<GalleryItem>,
    private val context: Context,
    private val onLongClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<GifViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GifViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return GifViewHolder(binding, context, onLongClickListener)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val item = galleryItems[position]
        holder.bind(item)
    }

    override fun getItemCount() = galleryItems.size
}
