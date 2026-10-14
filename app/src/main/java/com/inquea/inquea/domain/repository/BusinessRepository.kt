package com.inquea.inquea.domain.repository

import android.net.Uri
import com.inquea.inquea.domain.model.BusinessProfile
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {
    fun saveBusinessProfile(profile: BusinessProfile): Flow<Resource<Unit>>
    fun updateBusinessProfile(specialty: String, description: String, mediaUri: Uri?, isVideo: Boolean): Flow<Resource<Unit>>
    fun getBusinessProfile(businessId: String): Flow<Resource<BusinessProfile>>
    fun searchBusinesses(query: String): Flow<Resource<List<BusinessProfile>>>
}
