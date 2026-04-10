package com.inquea.inquea.domain.repository

import com.inquea.inquea.domain.model.UserProfile
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<String>>
    fun register(
        email: String, 
        password: String, 
        role: String, 
        name: String, 
        businessName: String? = null, 
        address: String? = null, 
        phone: String? = null
    ): Flow<Resource<String>>
    fun getCurrentUserRole(): Flow<Resource<String?>>
    fun getCurrentUserProfile(): Flow<Resource<UserProfile?>>
    fun logout()
}
