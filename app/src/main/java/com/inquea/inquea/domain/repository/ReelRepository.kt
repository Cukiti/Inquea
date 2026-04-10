package com.inquea.inquea.domain.repository

import android.net.Uri
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReelRepository {
    fun uploadReel(videoUri: Uri, title: String, tags: List<String>): Flow<Resource<Unit>>
    fun getReels(): Flow<Resource<List<BusinessReel>>>
}
