package com.inquea.inquea.di

import com.inquea.inquea.data.repository.AuthRepositoryImpl
import com.inquea.inquea.data.repository.BookingRepositoryImpl
import com.inquea.inquea.data.repository.BusinessRepositoryImpl
import com.inquea.inquea.data.repository.ChatRepositoryImpl
import com.inquea.inquea.data.repository.FlashOfferRepositoryImpl
import com.inquea.inquea.data.repository.ReelRepositoryImpl
import com.inquea.inquea.domain.repository.AuthRepository
import com.inquea.inquea.domain.repository.BookingRepository
import com.inquea.inquea.domain.repository.BusinessRepository
import com.inquea.inquea.domain.repository.ChatRepository
import com.inquea.inquea.domain.repository.FlashOfferRepository
import com.inquea.inquea.domain.repository.ReelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindBusinessRepository(
        businessRepositoryImpl: BusinessRepositoryImpl
    ): BusinessRepository

    @Binds
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    abstract fun bindFlashOfferRepository(
        flashOfferRepositoryImpl: FlashOfferRepositoryImpl
    ): FlashOfferRepository
    
    @Binds
    abstract fun bindReelRepository(
        reelRepositoryImpl: ReelRepositoryImpl
    ): ReelRepository
    
    @Binds
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}
