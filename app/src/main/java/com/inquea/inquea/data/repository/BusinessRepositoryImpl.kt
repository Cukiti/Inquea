package com.inquea.inquea.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.inquea.inquea.domain.model.BusinessProfile
import com.inquea.inquea.domain.repository.BusinessRepository
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class BusinessRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : BusinessRepository {

    override fun saveBusinessProfile(profile: BusinessProfile): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val profileWithId = profile.copy(id = userId)
                firestore.collection("businesses").document(userId).set(profileWithId).await()
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("User not logged in"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun updateBusinessProfile(specialty: String, description: String, mediaUri: Uri?, isVideo: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            
            var mediaUrl = ""
            
            if (mediaUri != null) {
                val extension = if (isVideo) ".mp4" else ".jpg"
                val ref = storage.reference.child("profiles/$userId/media${extension}")
                ref.putFile(mediaUri).await()
                mediaUrl = ref.downloadUrl.await().toString()
            }
            
            val updates = mutableMapOf<String, Any>(
                "specialty" to specialty,
                "description" to description
            )
            
            if (mediaUrl.isNotEmpty()) {
                updates["mediaUrl"] = mediaUrl
            }
            
            firestore.collection("businesses").document(userId).set(updates, SetOptions.merge()).await()
            
            emit(Resource.Success(Unit))
            
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun getBusinessProfile(businessId: String): Flow<Resource<BusinessProfile>> = flow {
        emit(Resource.Loading())
        try {
            val doc = firestore.collection("businesses").document(businessId).get().await()
            val profile = doc.toObject(BusinessProfile::class.java)
            if (profile != null) {
                emit(Resource.Success(profile))
            } else {
                emit(Resource.Error("Profile not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
    
    override fun searchBusinesses(query: String): Flow<Resource<List<BusinessProfile>>> = callbackFlow {
        trySend(Resource.Loading())
        
        // Very basic search, getting all and filtering client-side for simplicity in this demo phase.
        // In a real scenario, use Algolia or Typesense for full-text search.
        val listenerRegistration = firestore.collection("businesses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val allBusinesses = snapshot.documents.mapNotNull { it.toObject(BusinessProfile::class.java) }
                    val filtered = if (query.isBlank()) {
                        allBusinesses
                    } else {
                        allBusinesses.filter { 
                            it.name.contains(query, ignoreCase = true) || 
                            it.specialty.contains(query, ignoreCase = true)
                        }
                    }
                    trySend(Resource.Success(filtered))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }
}
