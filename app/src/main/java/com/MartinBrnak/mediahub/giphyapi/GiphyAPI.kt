package com.MartinBrnak.mediahub.giphyapi

import retrofit2.http.GET

private const val API_KEY = "BhqtMCD7aRWa9559K7YagiNgM545wOIk"

interface GiphyAPI {
    @GET("trending?api_key=$API_KEY&limit=25")
    suspend fun fetchGifs(): GiphyResponse
}