package com.inquea.inquea.domain.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String? = null
)
