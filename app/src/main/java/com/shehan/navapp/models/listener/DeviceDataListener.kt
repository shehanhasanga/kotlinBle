package com.shehan.navapp.models.listener

import com.shehan.navapp.models.BleDevice

interface DeviceDataListener {
    fun onDeviceDataReceived(dataMap : Map<String, BleDevice>)
}