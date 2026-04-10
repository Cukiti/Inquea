package com.inquea.inquea.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.model.UserProfile
import com.inquea.inquea.domain.repository.AuthRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<UserProfile?>?>(null)
    val profileState: StateFlow<Resource<UserProfile?>?> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserProfile().collect { result ->
                _profileState.value = result
            }
        }
    }
}
