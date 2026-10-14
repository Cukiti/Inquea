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
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _bookingState = MutableStateFlow<Resource<Unit>?>(null)
    val bookingState: StateFlow<Resource<Unit>?> = _bookingState.asStateFlow()

    fun createBooking(businessId: String, date: String, time: String, service: String = "Servicio Standard") {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "Cliente" // Placeholder for now

        val booking = Booking(
            businessId = businessId,
            clientId = userId,
            clientName = userName,
            date = date,
            time = time,
            service = service
        )

        viewModelScope.launch {
            repository.createBooking(booking).collect { result ->
                _bookingState.value = result
            }
        }
    }

    fun resetState() {
        _bookingState.value = null
    }
}
