package com.inquea.inquea.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.inquea.inquea.domain.model.Message
import com.inquea.inquea.domain.repository.ChatRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val repository: ChatRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _messagesState = MutableStateFlow<Resource<List<Message>>>(Resource.Loading())
    val messagesState: StateFlow<Resource<List<Message>>> = _messagesState.asStateFlow()

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            repository.getMessages(chatId).collect { result ->
                _messagesState.value = result
            }
        }
    }

    fun sendMessage(chatId: String, text: String, receiverId: String, receiverName: String) {
        viewModelScope.launch {
            repository.sendMessage(chatId, text, receiverId, receiverName).collect {
                // Not observing the result here, as messages stream will update
            }
        }
    }
}
