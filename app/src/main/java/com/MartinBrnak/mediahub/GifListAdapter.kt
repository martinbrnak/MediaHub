package com.MartinBrnak.mediahub

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.MartinBrnak.mediahub.giphyapi.GalleryItem
import com.MartinBrnak.mediahub.databinding.ListItemGalleryBinding
import com.bumptech.glide.Glide

class GifViewHolder(
    private val binding: ListItemGalleryBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {
    private val longPressThreshold = 350 // Time threshold for long press in milliseconds
    private var longPressHandler: Handler? = null
    private var isLongPress = false

    @SuppressLint("ClickableViewAccessibility")
    fun bind(galleryItem: GalleryItem, onGifHold: () -> Unit) {
        Glide.with(context)
            .load(galleryItem.images.original.url)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.itemImageView)


        /*binding.root.setOnClickListener{
            onGifHold()
        }*/



        binding.root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    longPressHandler = Handler()
                    longPressHandler?.postDelayed({
                        isLongPress = true
                        Toast.makeText(context, "Long Press!", LENGTH_LONG).show()
                        //onGifHold(galleryItem)
                    }, longPressThreshold.toLong())
                    true
                }
                MotionEvent.ACTION_UP -> {
                    longPressHandler?.removeCallbacksAndMessages(null)
                    if (!isLongPress) {
                        onGifHold()

                        // This is a short press
                        // Perform action for short press (e.g., open the image in a detail view)
                    }
                    isLongPress = false
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    longPressHandler?.removeCallbacksAndMessages(null)
                    isLongPress = false
                    true
                }
                else -> false
            }
    }
}
}

class GifListAdapter(
    private val galleryItems: List<GalleryItem>,
    private val context: Context,
    private val onGifHold: (GalleryItem) -> Unit
) : RecyclerView.Adapter<GifViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GifViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return GifViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val item = galleryItems[position]
        holder.bind(item){
            onGifHold(item)
        }
    }

    override fun getItemCount() = galleryItems.size
}
