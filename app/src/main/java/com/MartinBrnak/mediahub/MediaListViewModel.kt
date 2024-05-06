package com.MartinBrnak.mediahub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaListViewModel : ViewModel() {
    private val mediaRepository = MediaRepository.get()

    private val _mediaAll: MutableStateFlow<List<Media>> = MutableStateFlow(emptyList())
    val mediaAll: StateFlow<List<Media>>
        get() = _mediaAll.asStateFlow()

    init {
        viewModelScope.launch {
            mediaRepository.getMediaAll().collect {
                _mediaAll.value = it
            }
        }
    }

    suspend fun addMedia(media: Media) {
        mediaRepository.addMedia(media)
    }
}
