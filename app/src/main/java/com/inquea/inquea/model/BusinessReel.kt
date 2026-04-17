package com.inquea.inquea.model

data class BusinessReel(
    val id: String = "",
    val businessId: String = "",
    val businessName: String = "",
    val description: String = "",
    val rating: Float = 5.0f,
    val specialty: String = "",
    val tags: List<String> = emptyList(),
    val mediaUrl: String = "",
    val isVideo: Boolean = true,
    val hasFlashOffer: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)