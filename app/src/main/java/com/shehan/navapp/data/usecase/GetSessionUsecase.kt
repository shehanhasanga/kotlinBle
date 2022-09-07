package com.shehan.navapp.data.usecase

import com.shehan.navapp.data.repository.SessionRepository
import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.models.Session
import com.shehan.navapp.models.TherapyConfig

class GetSessionUsecase(private val sessionRepository: SessionRepository) {
    suspend fun execute(): Resource<Session> {
        var sessionData = sessionRepository.getSessions()
        var updateddata = sessionData.data?.let { addProgress(it) }
        sessionData.data = updateddata

        return sessionData
    }

    fun addProgress(session : Session) : Session{
        var therapyConfigs = session.therapyList
        var updatedTherapyConfig  = mutableListOf<TherapyConfig>();
        var elapseTime =  session.elapseTime
        var timeSofar = 0
        for(therapy in therapyConfigs){
            timeSofar += therapy.time
            if(timeSofar < elapseTime!!){
                therapy.progress = therapy.time
            }else {
                if((timeSofar - elapseTime) > therapy.time){
                    therapy.progress = 0
                } else{
                    therapy.progress = therapy.time-(timeSofar - elapseTime)
                }
            }
            println(therapy.progress.toString() + "progress in usecase")
            updatedTherapyConfig.add(therapy)
        }
        session.therapyList = updatedTherapyConfig.toTypedArray()
        return session
    }
}