package com.shehan.navapp.models

import android.os.Handler
import android.os.Looper

class DeviceUpdator(deviceId: String) {
    var handler: Handler = Handler(Looper.getMainLooper())
    lateinit var runnable: Runnable

    fun start(){
        handler.post(runnable)
    }

    fun stop(){
        println("device stop is called+++++")
        handler.removeCallbacks(runnable)
    }

}