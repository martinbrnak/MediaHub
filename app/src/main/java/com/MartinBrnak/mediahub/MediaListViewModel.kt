package com.MartinBrnak.mediahub

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class MediaListViewModel : ViewModel() {
    private val mediaRepository = MediaRepository.get()

    private val _mediaAll: MutableStateFlow<List<Media>> = MutableStateFlow(emptyList())
    val mediaAll: StateFlow<List<Media>>
        get() = _mediaAll.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val items = mediaRepository.getMediaAll().collect() { items ->
                Log.d("MediaListViewModel", "Items received: $items")
                _mediaAll.value = items
            }
            } catch (ex: Exception) {
                Log.e("MediaListViewModel", "Failed to fetch gallery items", ex)
            }
        }
    }

    suspend fun addMedia(media: Media) {
        mediaRepository.addMedia(media)
    }
}
