package com.shehan.navapp.data.usecase

import com.shehan.navapp.data.repository.SessionRepository
import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.models.Session
import com.shehan.navapp.models.TherapyConfig

class UpdateSessionUsecase(private val sessionRepository: SessionRepository) {
    suspend fun execute(elapseTime : Int, deviceId :String): Resource<Session> {
        return sessionRepository.updateSessions(elapseTime, deviceId)
    }


}