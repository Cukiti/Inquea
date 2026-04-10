package com.inquea.inquea.domain.repository

import com.inquea.inquea.domain.model.Booking
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun createBooking(booking: Booking): Flow<Resource<Unit>>
    fun getBusinessBookings(businessId: String): Flow<Resource<List<Booking>>>
}
