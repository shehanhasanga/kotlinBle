package com.anushka.newsapiclient.presentation.di

import com.shehan.navapp.data.api.SessionAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
         return Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("http://192.168.43.212:9000")
             .build()
    }

    @Singleton
    @Provides
    fun provideNewsAPIService(retrofit: Retrofit):SessionAPIService{
        return retrofit.create(SessionAPIService::class.java)
    }



}













