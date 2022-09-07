package com.shehan.navapp.service

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.shehan.navapp.models.BleDevice
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleService : Service() {
    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val TAG = "ble service"
    private var connectionState = STATE_DISCONNECTED
    private var connectedDevicemyMap = mutableMapOf<String,BleDevice>()
    private val _uiState = MutableStateFlow(connectedDevicemyMap)
    val uiState: StateFlow<Map<String,BleDevice>> = _uiState

//    val deviceMapLiveData: Flow<List<Map<String,BleDevice>>>? = null

    suspend fun sendData() {
        _uiState.emit(connectedDevicemyMap)
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2

    }


    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                CoroutineScope(Dispatchers.Default).launch {
                    sendData()
                }

                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    fun connect(address: String): Boolean {
        println("connect to device is called ")
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                } else{

                    var bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                    connectedDevicemyMap.put(bluetoothGatt.device.address, BleDevice(bluetoothGatt.device.address, gatt = bluetoothGatt))
                    return  true
                }

            } catch (exception: IllegalArgumentException) {

                Log.w(TAG, "Device not found with provided address.")
                return false
            }
            // connect to the GATT server on the device
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }

    private fun close(bluetoothGatt: BluetoothGatt) {
        bluetoothGatt?.let { gatt ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else{
                gatt.close()
            }

        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        for (i in connectedDevicemyMap.keys){
            if(connectedDevicemyMap.get(i)?.gatt != null){
                close(connectedDevicemyMap.get(i)?.gatt!!)
            }

        }

        return super.onUnbind(intent)
    }


    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() : BleService {
            return this@BleService
        }
    }

}