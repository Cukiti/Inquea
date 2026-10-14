package com.inquea.inquea.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.inquea.inquea.domain.model.FlashOffer
import com.inquea.inquea.domain.repository.FlashOfferRepository
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FlashOfferRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FlashOfferRepository {

    override fun createFlashOffer(offer: FlashOffer): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val ref = firestore.collection("flash_offers").document()
                val offerWithDetails = offer.copy(id = ref.id, businessId = userId, timestamp = System.currentTimeMillis())
                ref.set(offerWithDetails).await()
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("User not logged in"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun getFlashOffers(): Flow<Resource<List<FlashOffer>>> = callbackFlow {
        trySend(Resource.Loading())
        val listenerRegistration = firestore.collection("flash_offers")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val offers = snapshot.documents.mapNotNull { it.toObject(FlashOffer::class.java) }
                    trySend(Resource.Success(offers))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }
}
