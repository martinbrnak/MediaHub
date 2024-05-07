package com.MartinBrnak.mediahub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MediaDetailViewModel(mediaId: UUID, mediaRepository: MediaRepository) : ViewModel() {

    private val _media: MutableStateFlow<Media?> = MutableStateFlow(null)
    val media: StateFlow<Media?> = _media.asStateFlow()

    init {
        viewModelScope.launch {
            _media.value = mediaRepository.getMedia(mediaId)
        }
    }

    fun updateMedia(onUpdate: (Media) -> Media) {
        _media.update { oldMedia ->
            oldMedia?.let { onUpdate(it) }
        }
    }


}

class MediaDetailViewModelFactory(
    private val mediaId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaDetailViewModel(mediaId, MediaRepository.get()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}