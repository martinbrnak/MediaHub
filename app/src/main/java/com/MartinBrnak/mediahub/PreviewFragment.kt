package com.MartinBrnak.mediahub

import android.app.DownloadManager
import android.content.Context
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
import java.io.File
import java.io.FileInputStream

class PreviewFragment : Fragment() {

    private var _binding: PreviewOverlayBinding? = null
    private val binding get() = _binding!!
    val API_KEY : AccessToken = '8787a8ab9a9b9ace57482b55f2868258091f5392'


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PreviewOverlayBinding.inflate(inflater, container, false)
        return binding.root
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


            val request = DownloadManager.Request(downloadUri)
                .setTitle("Downloading GIF")
                .setDescription("Downloading GIF file")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.gif") // Set destination directory and file name

            downloadManager.enqueue(request)
            // Create a File object for the downloaded GIF file
            val gifFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "image.gif")

            // Check if the GIF file exists
            if (gifFile.exists()) {
                // Create a Video object and set its content to the GIF file
                val videoContent: ByteString = ByteString.readFrom(FileInputStream(gifFile))
                val video = MediaStore.Video(videoContent)


                // Create a VideoIntelligenceServiceClient using the authentication credentials
                val serviceClient = VideoIntelligenceServiceClient.API_KEY

                // Use the client to make an API request to analyze the video
                val request = AnnotateVideoRequest.newBuilder()
                    .setInputContent(video)
                    .addFeatures(Feature.LABEL_DETECTION)
                    .build()
                val response = serviceClient.annotateVideo(request)

                // Handle the response to extract the detected objects or any other relevant information
                // For example, you can log the response or display it in a toast
                Log.d("VideoAnalysis", "Response: $response")
            } else {
                // GIF file does not exist
                Toast.makeText(context, "GIF file not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Load the GIF using the imageUrl
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun analyzeGifWithAPI(gifFile: File) {
        try {
            // Set up the credentials provider using the API key
            val credentials = GoogleCredentials.create(API_KEY).createScoped("https://www.googleapis.com/auth/cloud-platform")
            val credentialsProvider = FixedCredentialsProvider.create(credentials)

            // Create the Video Intelligence client
            val videoIntelligenceClient = VideoIntelligenceServiceClient.create(VideoIntelligenceServiceSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

            // Read the GIF file as an input stream
            val inputStream: InputStream = FileInputStream(gifFile)

            // Create the request with the input content (GIF file)
            val request = AnnotateVideoRequest.newBuilder()
                .setInputContent(com.google.protobuf.ByteString.readFrom(inputStream))
                .build()

            // Perform the object tracking annotation on the GIF file
            val response = videoIntelligenceClient.annotateVideo(request)

            // Process the response (e.g., extract objects detected)
            // You can access the response data using response.annotationResults
            // This function will handle the response data and display it (e.g., in a Toast or log message)
            processApiResponse(response)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to analyze GIF with API", Toast.LENGTH_SHORT).show()
        }
    }

    // You can add additional methods here to update the content of the preview fragment
}