package com.inquea.inquea.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.repository.AuthRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<String>?>(null)
    val authState: StateFlow<Resource<String>?> = _authState.asStateFlow()

    private val _roleState = MutableStateFlow<Resource<String?>?>(null)
    val roleState: StateFlow<Resource<String?>?> = _roleState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                _authState.value = result
            }
        }
    }

    fun register(
        email: String, 
        password: String, 
        role: String, 
        name: String,
        businessName: String? = null,
        address: String? = null,
        phone: String? = null
    ) {
        viewModelScope.launch {
            repository.register(email, password, role, name, businessName, address, phone).collect { result ->
                _authState.value = result
            }
        }
    }

    fun checkCurrentUserRole() {
        viewModelScope.launch {
            repository.getCurrentUserRole().collect { result ->
                _roleState.value = result
            }
        }
    }
    
    fun logout() {
        repository.logout()
        _authState.value = null
        _roleState.value = null
    }
    
    fun resetState() {
        _authState.value = null
    }
}
