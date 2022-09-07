package com.anushka.newsapiclient.presentation.di

import com.shehan.navapp.data.repository.SessionRepository
import com.shehan.navapp.data.usecase.GetSessionUsecase
import com.shehan.navapp.data.usecase.UpdateSessionUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
   @Singleton
   @Provides
   fun provideGetNewsheadLinesUseCase(
       sessionRepository: SessionRepository
   ):GetSessionUsecase{
      return GetSessionUsecase(sessionRepository)
   }

   @Singleton
   @Provides
   fun provideupdateSession(
      sessionRepository: SessionRepository
   ):UpdateSessionUsecase{
      return UpdateSessionUsecase(sessionRepository)
   }
}


















