package com.inquea.inquea.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.inquea.inquea.domain.model.Chat
import com.inquea.inquea.domain.repository.ChatRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _chatsState = MutableStateFlow<Resource<List<Chat>>>(Resource.Loading())
    val chatsState: StateFlow<Resource<List<Chat>>> = _chatsState.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.getChats().collect { result ->
                _chatsState.value = result
            }
        }
    }
}
