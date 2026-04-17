package com.inquea.inquea.domain.model

data class Booking(
    val id: String = "",
    val businessId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val date: String = "",
    val time: String = "",
    val service: String = "",
    val status: String = "Pending", // Pending, Confirmed, Cancelled
    val price: Double = 25.0, // Hardcoded for prototype
    val paidWithCukitis: Boolean = false // Integración de moneda premium
)
