package com.shehan.navapp.data.repository

import com.shehan.navapp.data.datasource.SessionRemoteDataSource
import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.models.Session
import retrofit2.Response

class SessionRepositoryImpl(
    private val sessionRemoteDataSource: SessionRemoteDataSource
): SessionRepository {
    override suspend fun getSessions(): Resource<Session> {
        return responseToResource(sessionRemoteDataSource.getSession())
    }

    override suspend fun updateSessions(elapseTime : Int, deviceId :String): Resource<Session> {
        return responseToResource(sessionRemoteDataSource.updateSession(elapseTime,deviceId))
    }

    private fun responseToResource(response: Response<Session>):Resource<Session>{
        if(response.isSuccessful){
            response.body()?.let {result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(response.message())
    }

}