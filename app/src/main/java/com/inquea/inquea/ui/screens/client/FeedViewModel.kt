package com.inquea.inquea.ui.screens.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.repository.ReelRepository
import com.inquea.inquea.domain.repository.ChatRepository
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: ReelRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow<Resource<List<BusinessReel>>>(Resource.Loading())
    val feedState: StateFlow<Resource<List<BusinessReel>>> = _feedState.asStateFlow()

    private val _chatState = MutableStateFlow<Resource<String>?>(null)
    val chatState: StateFlow<Resource<String>?> = _chatState.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            repository.getReels().collect { result ->
                _feedState.value = result
            }
        }
    }
    
    fun startChat(businessId: String, businessName: String) {
        viewModelScope.launch {
            chatRepository.createOrGetChat(businessId, businessName).collect { result ->
                _chatState.value = result
            }
        }
    }
    
    fun resetChatState() {
        _chatState.value = null
    }
}
