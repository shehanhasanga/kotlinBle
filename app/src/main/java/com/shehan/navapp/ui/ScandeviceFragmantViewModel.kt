package com.shehan.navapp.ui

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.shehan.navapp.models.BleDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScandeviceFragmantViewModel : ViewModel() {
     val scandeviceMap = mutableMapOf<String, BluetoothDevice>()
    val deviceArray: MutableList<BleDevice> = mutableListOf<BleDevice>()
    init {
        viewModelScope.launch {

//            bluetoothService!!.messages().collect { uiState ->
//                println("new event is camme +++++++++++")
//            }
        }
    }

    fun setScandevices () {

    }
}