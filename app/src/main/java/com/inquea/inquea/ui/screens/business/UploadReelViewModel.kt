package com.inquea.inquea.ui.screens.business

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.repository.ReelRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadReelViewModel @Inject constructor(
    private val repository: ReelRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<Resource<Unit>?>(null)
    val uploadState: StateFlow<Resource<Unit>?> = _uploadState.asStateFlow()

    fun uploadReel(videoUri: Uri, title: String, tagsString: String) {
        val tags = tagsString.split(" ").map { it.removePrefix("#") }.filter { it.isNotBlank() }
        viewModelScope.launch {
            repository.uploadReel(videoUri, title, tags).collect { result ->
                _uploadState.value = result
            }
        }
    }

    fun resetState() {
        _uploadState.value = null
    }
}
