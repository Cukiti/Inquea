package com.inquea.inquea.ui.screens.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.inquea.inquea.domain.model.Booking
import com.inquea.inquea.domain.repository.BookingRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientBookingsViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _bookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val bookingsState: StateFlow<Resource<List<Booking>>> = _bookingsState.asStateFlow()

    init {
        fetchClientBookings()
    }

    private fun fetchClientBookings() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.getClientBookings(userId).collect { result ->
                _bookingsState.value = result
            }
        }
    }
}
