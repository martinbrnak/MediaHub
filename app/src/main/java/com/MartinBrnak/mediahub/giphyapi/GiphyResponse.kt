package com.MartinBrnak.mediahub.giphyapi

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GiphyResponse(
    val data: List<GalleryItem>
)
