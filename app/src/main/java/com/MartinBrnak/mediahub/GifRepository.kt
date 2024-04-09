package com.MartinBrnak.mediahub

import com.MartinBrnak.mediahub.giphyapi.GiphyAPI
import com.MartinBrnak.mediahub.giphyapi.GalleryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class GifRepository {
    private val giphyAPI: GiphyAPI

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/gifs/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        giphyAPI = retrofit.create()
    }

    suspend fun fetchGifs(): List<GalleryItem> =
        giphyAPI.fetchGifs().data
}