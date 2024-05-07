package com.MartinBrnak.mediahub.giphyapi

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "BhqtMCD7aRWa9559K7YagiNgM545wOIk"

interface GiphyAPI {
    @GET("trending?api_key=$API_KEY&limit=30")
    suspend fun fetchTrendingGifs(): GiphyResponse

    @GET("search?api_key=$API_KEY&limit=30")
    suspend fun searchGifs(@Query("q") query: String): GiphyResponse
}