package com.inquea.inquea.ui.screens.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.model.BusinessProfile
import com.inquea.inquea.domain.repository.BusinessRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessProfileViewModel @Inject constructor(
    private val repository: BusinessRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    fun saveProfile(name: String, specialty: String, location: String, description: String) {
        val profile = BusinessProfile(
            name = name,
            specialty = specialty,
            location = location,
            description = description
        )
        viewModelScope.launch {
            repository.saveBusinessProfile(profile).collect { result ->
                _saveState.value = result
            }
        }
    }
    
    fun resetState() {
        _saveState.value = null
    }
}
