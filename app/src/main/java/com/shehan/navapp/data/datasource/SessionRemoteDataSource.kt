package com.shehan.navapp.data.datasource

import com.shehan.navapp.data.api.SessionAPIService
import com.shehan.navapp.models.Session
import retrofit2.Response

class SessionRemoteDataSource(
    private val sessionApiService: SessionAPIService
) {

    suspend fun getSession(): Response<Session> {
        return sessionApiService.getSession()
    }

    suspend fun updateSession(elapseTime : Int, deviceId :String): Response<Session> {
        return sessionApiService.updateSession(UpdateData(elapseTime))
    }

     class UpdateData(val elapseTime:Int){

    }


}