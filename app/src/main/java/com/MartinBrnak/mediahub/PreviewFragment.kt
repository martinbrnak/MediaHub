package com.MartinBrnak.mediahub

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.MartinBrnak.mediahub.R
import com.MartinBrnak.mediahub.databinding.PreviewOverlayBinding
import com.bumptech.glide.Glide
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.videointelligence.v1.AnnotateVideoRequest
import com.google.cloud.videointelligence.v1.Feature
import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient
import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceSettings
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.UUID

class PreviewFragment : Fragment() {

    private var _binding: PreviewOverlayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PreviewOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var downloadManager: DownloadManager
    private var downloadId: Long = -1

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
                val receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (receivedDownloadId == downloadId) {
                    // The download with the specified ID is complete
                }
            }
        }
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: PreviewFragmentArgs by navArgs()
        val imageUrl = args.gifUrl
        val imageView = view.findViewById<ImageView>(R.id.previewImageView)
        var button: Button = view.findViewById(R.id.downloadButton)

        button.setOnClickListener {
            val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(imageUrl)
            val mediaRepository = MediaRepository.get()
            val mediaId = UUID.randomUUID()

            val request = DownloadManager.Request(downloadUri)
                .setTitle("Downloading GIF")
                .setDescription("Downloading GIF file")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$mediaId.gif") // Set destination directory and file name

            downloadId = downloadManager.enqueue(request)

            // Insert the Media object into the database
            val newMedia = Media(
                id = mediaId,
                title = "Title", // Set your desired title
                description = "Description", // Set your desired description
                format = "GIF", // Set the format to GIF
                url = imageUrl // Set the URL of the GIF
            )

            // Launch a coroutine to insert the media into the database
            lifecycleScope.launch {
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
                    val gifFile = File(downloadDirectory, "$mediaId.gif")
                    if (gifFile.exists()) {
                        val detectedLabels = processDownloadedFile(gifFile)
                        newMedia.description = "Detected Labels: ${detectedLabels.joinToString(", ")}"
                        mediaRepository.updateMedia(newMedia)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "GIF file not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

        // Load the GIF using the imageUrl
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imageView)
    }

    override fun onStart() {
        super.onStart()
        // Register the BroadcastReceiver to listen for download completion events
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        requireContext().registerReceiver(downloadReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Unregister the BroadcastReceiver when the fragment is stopped
        requireContext().unregisterReceiver(downloadReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private suspend fun processDownloadedFile(gifFile: File): List<String> {
        Log.d("VideoAnalysis", "Processing downloaded file: ${gifFile.absolutePath}")

        if (gifFile.exists()) {
            try {
                Log.d("VideoAnalysis", "Loading JSON key file")
                Log.d("We will open:", requireContext().assets.open("cs501-app-8787a8ab9a9b.json").toString())
                // Load the JSON key file from assets
                val jsonInputStream = requireContext().assets.open("cs501-app-8787a8ab9a9b.json")
                Log.d("JsonInput", jsonInputStream.toString())
                val credentials = GoogleCredentials.fromStream(jsonInputStream)

                Log.d("VideoAnalysis", "Creating VideoIntelligenceServiceClient")
                // Create a VideoIntelligenceServiceClient using the authentication credentials
                val serviceClient = VideoIntelligenceServiceClient.create(
                    VideoIntelligenceServiceSettings.newBuilder()
                        .setCredentialsProvider { credentials }
                        .build()
                )

                Log.d("VideoAnalysis", "Building AnnotateVideoRequest")
                // Use the client to make an API request to analyze the video
                val request = AnnotateVideoRequest.newBuilder()
                    .setInputContent(ByteString.readFrom(FileInputStream(gifFile)))
                    .addFeatures(Feature.LABEL_DETECTION)
                    .build()

                Log.d("VideoAnalysis", "Sending request to VideoIntelligenceServiceClient")
                // Send the request and wait for the response
                val response = serviceClient.annotateVideoAsync(request).get()

                // Handle the response and extract the detected labels
                val labels = response.annotationResultsList.flatMap { result ->
                    result.segmentLabelAnnotationsList.map { it.entity.description }
                }

                // Log the detected labels
                Log.d("VideoAnalysis", "Detected Labels: $labels")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Analysis Complete!", Toast.LENGTH_SHORT).show()
                }
                return labels
            } catch (e: Exception) {
                // Handle any errors that occur during the analysis
                Log.e("VideoAnalysis", "Error during video analysis", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Analysis Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // GIF file does not exist
            Log.e("VideoAnalysis", "GIF file not found: ${gifFile.absolutePath}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "GIF file not found", Toast.LENGTH_SHORT).show()
            }
        }
        return emptyList()
    }
}