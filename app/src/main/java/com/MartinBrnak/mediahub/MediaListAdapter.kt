package com.MartinBrnak.mediahub

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MartinBrnak.mediahub.databinding.HomeFragmentBinding
import com.MartinBrnak.mediahub.databinding.MediaItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import java.io.File
import java.util.UUID
import javax.sql.DataSource


class MediaViewHolder(
    private val binding: MediaItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(media: Media, onMediaClicked: (mediaId: UUID) -> Unit) {
        binding.title.text = media.title

        // Set up the TextSwitcher with a child TextView
        val textView = TextView(context)
        textView.setTextColor(context.getColor(android.R.color.white))
        textView.text = media.description

        binding.description.removeAllViews()
        binding.description.addView(textView)


        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val gifFile = File(downloadDirectory, "${media.id}.gif")
        Log.d("MediaViewHolder", "GIF file path: ${gifFile.absolutePath}")

        if (gifFile.exists()) {
            Glide.with(context)
                .asGif()
                .load(gifFile)
                .placeholder(R.drawable.loading_white)
                .error(R.drawable.magnifying_glass_icon) // Add an error placeholder image
                .into(binding.mediaView)
        } else {
            Log.e("MediaViewHolder", "GIF file not found: ${gifFile.absolutePath}")
            binding.mediaView.setImageResource(R.drawable.magnifying_glass_icon) // Set the error placeholder image
        }

        binding.root.setOnClickListener {
            onMediaClicked(media.id)
        }


    }
}

class MediaListAdapter(
    private val context: Context,
    private var mediaAll: List<Media>,
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

    fun updateMediaList(newMediaList: List<Media>) {
        mediaAll = newMediaList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaAll[position]
        holder.bind(media, onMediaClicked)
    }

    override fun getItemCount() = mediaAll.size
}
