package com.example.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.library.LibraryResourceModel
import com.example.domain.repository.library.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MediaViewerUiState(
    val resource: LibraryResourceModel? = null,
    val isPlaying: Boolean = true,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val playbackSpeed: Float = 1.0f,
    val isPictureInPictureEnabled: Boolean = false,
    val isAudioOnly: Boolean = false,
    val sleepTimerMinutes: Int = 0, // 0 = OFF
    val isLoading: Boolean = false,
    val error: String? = null
)

class MediaViewerViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaViewerUiState())
    val uiState: StateFlow<MediaViewerUiState> = _uiState.asStateFlow()

    fun loadMediaResource(resourceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val resResult = repository.getResourceById(resourceId)
            if (resResult.isSuccess) {
                val res = resResult.getOrNull()
                val isAudio = res?.fileType == com.example.domain.model.library.FileType.MP3 || res?.resourceType == com.example.domain.model.library.ResourceType.AUDIO_LECTURE
                _uiState.update {
                    it.copy(
                        resource = res,
                        isAudioOnly = isAudio,
                        durationMs = (res?.durationSeconds ?: 1800) * 1000L,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load media") }
            }
        }
    }

    fun togglePlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun seekTo(positionMs: Long) {
        _uiState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
    }

    fun setSleepTimer(minutes: Int) {
        _uiState.update { it.copy(sleepTimerMinutes = minutes) }
    }

    fun togglePictureInPicture(enabled: Boolean) {
        _uiState.update { it.copy(isPictureInPictureEnabled = enabled) }
    }
}
