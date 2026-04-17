package com.inquea.inquea.ui.screens.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.inquea.inquea.domain.model.Booking
import com.inquea.inquea.domain.repository.BookingRepository
import com.inquea.inquea.domain.repository.ReelRepository
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessMainViewModel @Inject constructor(
    private val repository: ReelRepository,
    private val bookingRepository: BookingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _myReelsState = MutableStateFlow<Resource<List<BusinessReel>>>(Resource.Loading())
    val myReelsState: StateFlow<Resource<List<BusinessReel>>> = _myReelsState.asStateFlow()

    private val _bookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val bookingsState: StateFlow<Resource<List<Booking>>> = _bookingsState.asStateFlow()

    init {
        loadMyReels()
        loadMyBookings()
    }

    private fun loadMyReels() {
        viewModelScope.launch {
            repository.getMyReels().collect { result ->
                _myReelsState.value = result
            }
        }
    }

    private fun loadMyBookings() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                bookingRepository.getBusinessBookings(userId).collect { result ->
                    _bookingsState.value = result
                }
            } else {
                _bookingsState.value = Resource.Error("Usuario no autenticado")
            }
        }
    }

    fun deleteReel(reelId: String) {
        viewModelScope.launch {
            repository.deleteReel(reelId).collect {
                // UI will automatically update due to Firestore snapshot listener in getMyReels
            }
        }
    }
}
