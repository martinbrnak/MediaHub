package com.MartinBrnak.mediahub

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.MartinBrnak.mediahub.databinding.ImportFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.SocketTimeoutException
import java.util.UUID

class ImportFragment : Fragment() {
    private var _binding: ImportFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImportFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedLink = arguments?.getString("sharedLink")
        if (sharedLink != null) {
            binding.editTextLink.setText(sharedLink)
        }

        binding.buttonImport.setOnClickListener {
            val link = binding.editTextLink.text.toString()
            if (link.isNotEmpty()) {
                downloadMedia(link)
            } else {
                // Show error message or toast indicating empty link
            }
        }
    }

    private fun downloadMedia(link: String) {
        // Check if the link is from TikTok or Instagram
        if (link.contains("tiktok.com") || link.contains("instagram.com")) {
            when {
                    link.contains("tiktok.com") -> {
                        val tiktokViewModel = TiktokViewModel()
                        val videoId = tiktokViewModel.extractVideoIdFromUrl(link)
                        if (videoId != null) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    tiktokViewModel.expandShortenedUrl(link)
                                    tiktokViewModel.getExpandedUrlLiveData().observe(viewLifecycleOwner) { expandedUrl ->
                                        val filename = "$videoId.mp4" // Generate filename based on video ID
                                        downloadAndCreateMedia(expandedUrl, filename, "TikTok")
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Failed to extract video ID from TikTok URL", Toast.LENGTH_LONG).show()
                        }

                    }

                link.contains("instagram.com") -> {
                // Download Instagram Reel
                }



            }
        }
        else {
            Toast.makeText(context, "Sorry we haven't implemented downloading from that site yet", Toast.LENGTH_LONG).show()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("Range")
    private fun downloadAndCreateMedia(downloadUrl: String, filename: String, source: String) {
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(downloadUrl)
        val mediaRepository = MediaRepository.get()
        val mediaId = UUID.randomUUID()

        val request = DownloadManager.Request(downloadUri)
            .setTitle("Downloading $filename")
            .setDescription("Downloading $source video")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$mediaId.mp4") // Set destination directory and file name

        val downloadId = downloadManager.enqueue(request)

        // Insert the Media object into the database
        val newMedia = Media(
            id = mediaId,
            title = filename, // Set the filename as the title
            description = "Source: $source", // Set the source as the description
            format = "MP4", // Set the format to MP4
            url = downloadUrl // Set the download URL
        )

        // Launch a coroutine to insert the media into the database
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mediaRepository.addMedia(newMedia)

                // Wait for the download to complete
                var downloadComplete = false
                while (!downloadComplete) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        downloadComplete = status == DownloadManager.STATUS_SUCCESSFUL
                    }
                    cursor.close()

                    if (!downloadComplete) {
                        delay(1000) // Wait for 1 second before checking again
                    }
                }

                // Download is complete, process the file
                val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val videoFile = File(downloadDirectory, "$mediaId.mp4")
                if (videoFile.exists()) {
                    // Process the downloaded video file if needed
                    // ...
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Video file not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
class TiktokViewModel : ViewModel() {
    private val tiktokRepo = TiktokRepo()

    private val expandedUrlLiveData = tiktokRepo.getExpandedUrlLiveData()
    fun getExpandedUrlLiveData(): LiveData<String> {
        return expandedUrlLiveData
    }

    fun expandShortenedUrl(shortenedUrl: String) {
        viewModelScope.launch {
            tiktokRepo.expandShortenedUrl(shortenedUrl)
        }
    }

    fun extractVideoIdFromUrl(url: String): String? {
        // Regular expression pattern to match the video ID in the URL
        val pattern = Regex("((?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:tiktok\\.com)\\/(?:@[\\w\\.-]+\\/)?(?:video|v)\\/)(\\d+)")
        val matchResult = pattern.find(url)
        return matchResult?.groupValues?.getOrNull(2)
    }
}

class TiktokRepo {
    private val expandedUrlLiveData = MutableLiveData<String>()

    fun getExpandedUrlLiveData(): LiveData<String> {
        return expandedUrlLiveData
    }

    suspend fun expandShortenedUrl(shortenedUrl: String) = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(shortenedUrl)
                .build()
            val response = client.newCall(request).execute()
            val expandedUrl = response.request.url.toString()
            expandedUrlLiveData.postValue(expandedUrl)
        } catch (e: SocketTimeoutException) {
            expandedUrlLiveData.postValue("Timeout occurred: ${e.message}")
        } catch (e: Exception) {
            expandedUrlLiveData.postValue("Error: ${e.message}")
        }
    }
}