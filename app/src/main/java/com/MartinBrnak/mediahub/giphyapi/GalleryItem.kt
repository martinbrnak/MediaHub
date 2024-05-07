package com.MartinBrnak.mediahub.giphyapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
@JsonClass(generateAdapter = true)
data class GalleryItem(
    val title: String,
    val id: String,
    val url: String,
    @Json(name = "embed_url") val embedUrl: String,
    val images: Images
) {
    @JsonClass(generateAdapter = true)
    data class Images(
        @Json(name = "original") val original: Original
    ) {
        @JsonClass(generateAdapter = true)
        data class Original(
            @Json(name = "url") val url: String
        )
    }
}*/

@JsonClass(generateAdapter = true)
data class GalleryItem(
    val type: String,
    val id: String,
    val url: String,
    @Json(name = "embed_url") val embedUrl: String,
    val username: String,
    val source: String,
    val title: String,
    val images: Images,
    @Json(name = "alt_text") val altText: String
) {
    @JsonClass(generateAdapter = true)
    data class Images(
        val original: Original,
    )




    @JsonClass(generateAdapter = true)
    data class Original(
        val height: String,
        val width: String,
        val size: String,
        val url: String,
    )

}
