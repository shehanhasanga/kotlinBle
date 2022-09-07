package com.anushka.newsapiclient.presentation.di

import com.shehan.navapp.data.datasource.SessionRemoteDataSource
import com.shehan.navapp.data.repository.SessionRepository
import com.shehan.navapp.data.repository.SessionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideNewsRepository(
        sessionRemoteDataSource: SessionRemoteDataSource
    ): SessionRepository {
        return SessionRepositoryImpl(
            sessionRemoteDataSource
        )
    }

}














