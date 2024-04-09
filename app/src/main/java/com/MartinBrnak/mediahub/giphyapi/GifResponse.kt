package com.MartinBrnak.mediahub.giphyapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class GifResponse(
    val galleryItems: List<GalleryItem>
)