package com.shehan.navapp.data.api

import com.shehan.navapp.data.datasource.SessionRemoteDataSource
import com.shehan.navapp.models.Session
import retrofit2.Response
import retrofit2.http.*

interface SessionAPIService {
    @POST("session")
    suspend fun getSession(
    ): Response<Session>

    @PUT("session")
    suspend fun updateSession(
        @Body
        updateData: SessionRemoteDataSource.UpdateData
    ): Response<Session>
}