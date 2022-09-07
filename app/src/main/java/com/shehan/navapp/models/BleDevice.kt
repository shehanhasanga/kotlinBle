package com.shehan.navapp.models

import android.bluetooth.BluetoothGatt

data class BleDevice (
    val deviceId: String,
    var gatt: BluetoothGatt? = null,
    var deviceName: String ? =  null,
    var session: Session? = null,
    var status : DeviceStatus? = null,
        ){
    var commandMap : Map<Int, List<String>> ? = null




}

