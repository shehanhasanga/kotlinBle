package com.shehan.navapp.data.repository

import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.models.Session

interface SessionRepository {
    suspend fun getSessions(): Resource<Session>
    suspend fun updateSessions(elapseTime : Int, deviceId :String): Resource<Session>
}