package com.MartinBrnak.mediahub

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
import androidx.navigation.fragment.navArgs
import com.MartinBrnak.mediahub.R
import com.MartinBrnak.mediahub.databinding.PreviewOverlayBinding
import com.bumptech.glide.Glide
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.videointelligence.v1.AnnotateVideoRequest
import com.google.cloud.videointelligence.v1.Feature
import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient
import com.google.protobuf.ByteString
import kotlinx.coroutines.awaitAll
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
                    handleDownloadCompletion()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: PreviewFragmentArgs by navArgs()
        val imageUrl = args.gifUrl
        val imageView = view.findViewById<ImageView>(R.id.previewImageView)
        var button : Button = view.findViewById(R.id.downloadButton)


        button.setOnClickListener{
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
            // Callback to process the downloaded file
            suspend{mediaRepository.
                addMedia(newMedia)
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

    private fun handleDownloadCompletion() {
        // The download is complete, proceed with further processing
        val gifFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "image.gif")
        if (gifFile.exists()) {
            // File exists, proceed with analyzing the downloaded file or any other processing
            // For example, you can initiate the analysis using the Video Intelligence API here
            processDownloadedFile(gifFile)
            Toast.makeText(context, "Download Complete!", Toast.LENGTH_SHORT).show()
        } else {
            // File not found
            Toast.makeText(context, "GIF file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processDownloadedFile(gifFile: File) {
        if (gifFile.exists()) {
            // Open the JSON key file as an input stream
            val inputStream: InputStream = requireContext().assets.open("cs501-app-8787a8ab9a9b.json")
            // Read the contents of the input stream into a ByteArray
            val byteArray: ByteArray = inputStream.readBytes()

            // Convert the ByteArray to a String (assuming the JSON key is encoded as UTF-8)
            val jsonString: String = byteArray.toString(Charsets.UTF_8)

            // Create a VideoIntelligenceServiceClient using the authentication credentials
            val serviceClient = VideoIntelligenceServiceClient.create()

            // Use the client to make an API request to analyze the video
            val request = AnnotateVideoRequest.newBuilder()
                .setInputContent(ByteString.readFrom(FileInputStream(gifFile)))
                .addFeatures(Feature.LABEL_DETECTION)
                .build()
            val response = serviceClient.annotateVideoAsync(request)
            Toast.makeText(context, "Analyzed!!", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Response: $response", Toast.LENGTH_LONG).show()
            // Handle the response to extract the detected objects or any other relevant information
            // For example, you can log the response or display it in a toast
            Log.d("VideoAnalysis", "Response: $response")
        } else {
            // GIF file does not exist
            Toast.makeText(context, "GIF file not found", Toast.LENGTH_SHORT).show()
        }
    }

    // You can add additional methods here to update the content of the preview fragment
}