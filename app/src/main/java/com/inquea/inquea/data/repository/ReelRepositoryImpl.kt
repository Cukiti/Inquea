package com.inquea.inquea.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.inquea.inquea.domain.repository.ReelRepository
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ReelRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ReelRepository {

    override fun uploadReel(mediaUri: Uri, title: String, tags: List<String>, isVideo: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            
            // Get business profile to populate reel metadata
            val profileDoc = firestore.collection("businesses").document(userId).get().await()
            val businessName = profileDoc.getString("name") ?: "Negocio"
            val specialty = profileDoc.getString("specialty") ?: ""
            
            // 1. Upload media to Storage
            val reelId = UUID.randomUUID().toString()
            val extension = if (isVideo) ".mp4" else ".jpg"
            val contentType = if (isVideo) "video/mp4" else "image/jpeg"
            val metadata = com.google.firebase.storage.storageMetadata {
                this.contentType = contentType
            }
            val storageRef = storage.reference.child("reels/$userId/$reelId$extension")
            storageRef.putFile(mediaUri, metadata).await()
            
            // 2. Get Download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // 3. Save to Firestore
            val newReel = BusinessReel(
                id = reelId,
                businessId = userId,
                businessName = businessName,
                description = title,
                specialty = specialty,
                tags = tags,
                mediaUrl = downloadUrl,
                isVideo = isVideo,
                timestamp = System.currentTimeMillis()
            )
            
            firestore.collection("reels").document(reelId).set(newReel).await()
            
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun getReels(): Flow<Resource<List<BusinessReel>>> = callbackFlow {
        trySend(Resource.Loading())
        val listenerRegistration = firestore.collection("reels")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val reels = snapshot.documents.mapNotNull { it.toObject(BusinessReel::class.java) }
                    trySend(Resource.Success(reels))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override fun getMyReels(): Flow<Resource<List<BusinessReel>>> = callbackFlow {
        trySend(Resource.Loading())
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }
        
        val listenerRegistration = firestore.collection("reels")
            .whereEqualTo("businessId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val reels = snapshot.documents.mapNotNull { it.toObject(BusinessReel::class.java) }
                        .sortedByDescending { it.timestamp }
                    trySend(Resource.Success(reels))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /* 
     * ==========================================
     * 👨‍💻 DESARROLLADO POR: JUAN DE DIOS
     * 💼 MÓDULO: Gestión de Reels, Storage y Ofertas
     * ==========================================
     */
    override fun deleteReel(reelId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            
            // Get the reel first to check if it's video
            val reelDoc = firestore.collection("reels").document(reelId).get().await()
            val isVideo = reelDoc.getBoolean("isVideo") ?: true
            
            // Delete from Firestore
            firestore.collection("reels").document(reelId).delete().await()
            
            // Delete from Storage
            val extension = if (isVideo) ".mp4" else ".jpg"
            val storageRef = storage.reference.child("reels/$userId/$reelId$extension")
            try {
                storageRef.delete().await()
            } catch (e: Exception) {
                // Ignore if storage deletion fails
            }
            
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred while deleting"))
        }
    }
}
