package com.shehan.navapp.models.listener

import com.shehan.navapp.models.BleDevice

interface DeviceConnectionListener {
    fun onConnectionSuccess(device: BleDevice, success: Boolean)
    fun onConnectionFail(device: BleDevice, success: Boolean)
}