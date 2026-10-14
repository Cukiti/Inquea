package com.inquea.inquea.domain.model

data class FlashOffer(
    val id: String = "",
    val businessId: String = "",
    val discount: String = "",
    val duration: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
