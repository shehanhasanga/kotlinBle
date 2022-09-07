package com.anushka.newsapiclient.presentation.di

import com.shehan.navapp.data.api.SessionAPIService
import com.shehan.navapp.data.datasource.SessionRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RemoteDataModule {

    @Singleton
    @Provides
    fun provideNewsRemoteDataSource(
        sessionApiService: SessionAPIService
    ):SessionRemoteDataSource{
       return SessionRemoteDataSource(sessionApiService)
    }

}












