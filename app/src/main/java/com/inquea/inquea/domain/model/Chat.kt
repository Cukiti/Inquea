package com.inquea.inquea.domain.model

data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(), // e.g. [clientId, businessId]
    val businessId: String = "",
    val clientId: String = "",
    val businessName: String = "",
    val clientName: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
