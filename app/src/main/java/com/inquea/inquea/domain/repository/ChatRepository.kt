package com.inquea.inquea.domain.repository

import com.inquea.inquea.domain.model.Chat
import com.inquea.inquea.domain.model.Message
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<Resource<List<Chat>>>
    fun getMessages(chatId: String): Flow<Resource<List<Message>>>
    fun sendMessage(chatId: String, text: String, receiverId: String, receiverName: String): Flow<Resource<Unit>>
    fun createOrGetChat(businessId: String, businessName: String): Flow<Resource<String>> // Returns chatId
}
