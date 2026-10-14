package com.inquea.inquea.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.inquea.inquea.domain.model.Booking
import com.inquea.inquea.domain.repository.BookingRepository
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override fun createBooking(booking: Booking): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val ref = firestore.collection("bookings").document()
            val bookingWithId = booking.copy(id = ref.id)
            ref.set(bookingWithId).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun getBusinessBookings(businessId: String): Flow<Resource<List<Booking>>> = callbackFlow {
        trySend(Resource.Loading())
        val listenerRegistration = firestore.collection("bookings")
            .whereEqualTo("businessId", businessId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
                    trySend(Resource.Success(bookings))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override fun getClientBookings(clientId: String): Flow<Resource<List<Booking>>> = callbackFlow {
        trySend(Resource.Loading())
        val listenerRegistration = firestore.collection("bookings")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
                    trySend(Resource.Success(bookings))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }
}
