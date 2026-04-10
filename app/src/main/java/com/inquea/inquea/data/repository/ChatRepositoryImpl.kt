package com.inquea.inquea.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.inquea.inquea.domain.model.Chat
import com.inquea.inquea.domain.model.Message
import com.inquea.inquea.domain.repository.ChatRepository
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ChatRepository {

    override fun getChats(): Flow<Resource<List<Chat>>> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return flow {
                emit(Resource.Loading())
                emit(Resource.Error("User not logged in"))
            }
        }

        return callbackFlow {
            trySend(Resource.Loading())
            
            val listenerRegistration = firestore.collection("chats")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.message ?: "Unknown error"))
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null) {
                        val chats = snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
                        trySend(Resource.Success(chats))
                    }
                }
                
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    override fun getMessages(chatId: String): Flow<Resource<List<Message>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val listenerRegistration = firestore.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    trySend(Resource.Success(messages))
                }
            }
            
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override fun sendMessage(chatId: String, text: String, receiverId: String, receiverName: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val timestamp = System.currentTimeMillis()
            
            // 1. Create Message
            val messageRef = firestore.collection("chats").document(chatId).collection("messages").document()
            val message = Message(
                id = messageRef.id,
                senderId = userId,
                text = text,
                timestamp = timestamp
            )
            messageRef.set(message).await()
            
            // 2. Update Chat Metadata
            val chatRef = firestore.collection("chats").document(chatId)
            val updates = mapOf(
                "lastMessage" to text,
                "lastMessageTimestamp" to timestamp
            )
            chatRef.update(updates).await()
            
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
    
    override fun createOrGetChat(businessId: String, businessName: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val clientId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val clientName = auth.currentUser?.displayName ?: "Cliente"
            
            // Check if chat exists
            val existingChatQuery = firestore.collection("chats")
                .whereArrayContains("participants", clientId)
                .get()
                .await()
                
            val existingChat = existingChatQuery.documents.find { 
                val participants = it.get("participants") as? List<*>
                participants?.contains(businessId) == true
            }
            
            if (existingChat != null) {
                emit(Resource.Success(existingChat.id))
            } else {
                // Create new chat
                val chatRef = firestore.collection("chats").document()
                val newChat = Chat(
                    id = chatRef.id,
                    participants = listOf(clientId, businessId),
                    businessId = businessId,
                    clientId = clientId,
                    businessName = businessName,
                    clientName = clientName,
                    lastMessage = "Chat iniciado",
                    lastMessageTimestamp = System.currentTimeMillis(),
                    unreadCount = 0
                )
                chatRef.set(newChat).await()
                emit(Resource.Success(chatRef.id))
            }
        } catch (e: Exception) {
             emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
