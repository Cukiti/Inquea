package com.inquea.inquea.domain.repository

import com.inquea.inquea.domain.model.FlashOffer
import com.inquea.inquea.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FlashOfferRepository {
    fun createFlashOffer(offer: FlashOffer): Flow<Resource<Unit>>
    fun getFlashOffers(): Flow<Resource<List<FlashOffer>>>
}
