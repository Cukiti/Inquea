package com.inquea.inquea.domain.repository

import android.net.Uri
import com.inquea.inquea.model.BusinessReel
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReelRepository {
    fun uploadReel(mediaUri: Uri, title: String, tags: List<String>, isVideo: Boolean): Flow<Resource<Unit>>
    fun getReels(): Flow<Resource<List<BusinessReel>>>
    fun getMyReels(): Flow<Resource<List<BusinessReel>>>
    fun deleteReel(reelId: String): Flow<Resource<Unit>>
}
