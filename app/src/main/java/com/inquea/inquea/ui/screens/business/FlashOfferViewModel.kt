package com.inquea.inquea.ui.screens.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.model.FlashOffer
import com.inquea.inquea.domain.repository.FlashOfferRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashOfferViewModel @Inject constructor(
    private val repository: FlashOfferRepository
) : ViewModel() {

    private val _createState = MutableStateFlow<Resource<Unit>?>(null)
    val createState: StateFlow<Resource<Unit>?> = _createState.asStateFlow()

    fun createOffer(discount: String, duration: String, description: String) {
        val offer = FlashOffer(
            discount = discount,
            duration = duration,
            description = description
        )
        viewModelScope.launch {
            repository.createFlashOffer(offer).collect { result ->
                _createState.value = result
            }
        }
    }

    fun resetState() {
        _createState.value = null
    }
}
