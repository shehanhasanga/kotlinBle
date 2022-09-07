package com.anushka.newsapiclient.presentation.di

import android.app.Application
import com.shehan.navapp.data.usecase.GetSessionUsecase
import com.shehan.navapp.ui.session.SessionViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {
    @Singleton
    @Provides
  fun provideSessionViewModelFactory(
     application: Application,
     getSessionUsecase: GetSessionUsecase
  ):SessionViewModelFactory{
      return SessionViewModelFactory(
          application,
          getSessionUsecase
      )
  }

}








