package com.shehan.navapp.service

import android.Manifest
import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.shehan.navapp.R
import com.shehan.navapp.data.usecase.GetSessionUsecase
import com.shehan.navapp.data.usecase.UpdateSessionUsecase
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.DeviceStatus
import com.shehan.navapp.models.DeviceUpdator
import com.shehan.navapp.models.Session
import com.shehan.navapp.models.listener.DeviceConnectionListener
import com.shehan.navapp.models.listener.DeviceDataListener
import com.shehan.navapp.models.listener.MessagesListener
import com.shehan.navapp.ui.session.SessionViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Runnable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.experimental.and
@AndroidEntryPoint
class BleServiceNew : Service() {
    @Inject
    lateinit var getSessionUsecase: GetSessionUsecase
    @Inject
    lateinit var updateSessionUsecase: UpdateSessionUsecase

    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val TAG = "ble service"
    private var connectionState = STATE_DISCONNECTED
    private var connectedDeviceMap = ConcurrentHashMap<String,BleDevice>()
    private var connectingDeviceMap : ConcurrentHashMap<String, BleDevice> = ConcurrentHashMap()
    private var deviceUpdatorMap : ConcurrentHashMap<String, DeviceUpdator> = ConcurrentHashMap()
    private var connectingDeviceCallbackMap : ConcurrentHashMap<String, DeviceConnectionListener> = ConcurrentHashMap()
    val _uiState = MutableStateFlow(connectedDeviceMap)
    private var messagesListenerglobal : MessagesListener? = null
    private lateinit var deviceDataListener : DeviceDataListener
    private val writeServiceUUIDString : String = "0000AE00-0000-1000-8000-00805f9b34fb"
    private var writeCharUUIDString : String = "0000AE01-0000-1000-8000-00805f9b34fb"
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    fun messages() = callbackFlow<String> {
        val messagesListener = object : MessagesListener {
            override fun onNewMessageReceived(message: String) {
                print("data recived in call back+++++")
                trySend(message)
            }
        }
        messagesListenerglobal = messagesListener
        awaitClose { /* unregister listener here */ }
    }

    fun deviceData() = callbackFlow<Map<String, BleDevice>> {
        val messagesListener = object : DeviceDataListener {
            override fun onDeviceDataReceived(dataMap: Map<String, BleDevice>) {
                trySend(dataMap)
            }
        }
        deviceDataListener = messagesListener
        awaitClose { /* unregister listener here */ }
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2

    }

//    @Synchronized
//    fun updateDeviceMap(deviceId:String, device: BleDevice){
//        connectingDeviceMap.put(deviceId, device)
//        sendData()
//    }

     fun sendData() {
         CoroutineScope(Dispatchers.Default).launch {
//             println("send data deom ble service +++++")
//             messagesListenerglobal?.onNewMessageReceived("message")
             deviceDataListener.onDeviceDataReceived(connectedDeviceMap)
//             _uiState.emit(connectedDeviceMap)
         }

    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                println("ble is connected")
//                connectedDevicemyMap.put(gatt?.device?.address!!, BleDevice(gatt?.device?.address !!, gatt = gatt))

                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                gatt?.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (gatt != null) {
                afterServiceDiscovered(gatt)
            }

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            val deviceStatus = decodeStatusData(characteristic!!)
            val device = connectedDeviceMap.get(gatt?.device?.address)
            device?.status = deviceStatus
            sendData()
//            if(device != null) {
//                var newDevice = BleDevice(device.deviceId,device.gatt,device.deviceName, device.session,deviceStatus)
//                updateDeviceMap(device.deviceId, newDevice)
//            }

        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            println("witten data  ++++++")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (gatt != null) {
                var handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable{
                    override fun run() {
                        writeCharacteristic(gatt, getString(R.string.pause_command))
                    }
                }, 1000)
//
            }
            println("descriptor value is received +++++++++++")
        }
    }

    @Synchronized
    fun afterServiceDiscovered(gatt: BluetoothGatt) {
        var device = gatt?.device?.let { connectingDeviceMap.get(it.address) }
        if (device != null){
            device.gatt = gatt
        }

        var service = gatt?.getService(UUID.fromString("0000AE00-0000-1000-8000-00805f9b34fb"))
        var char = service?.getCharacteristic(UUID.fromString("0000AE02-0000-1000-8000-00805f9b34fb"))
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        gatt?.setCharacteristicNotification(char, true)
        val descriptor = char?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        descriptor!!.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }

        gatt!!.writeDescriptor(descriptor)
        val deviceData: BluetoothDevice = gatt.device
//        var bleDevice : BleDevice = BleDevice(deviceData.address, gatt,deviceData.name,null, null)
//        connectedDeviceMap.put(deviceData.address, bleDevice)
//        val callback = connectingDeviceCallbackMap.get(deviceData.address)
//        callback?.onConnectionSuccess(BleDevice(deviceData.address, null, deviceData.name,null, null), true)
//        sendData()
//         ioScope = CoroutineScope(Dispatchers.IO + Job())
            ioScope.launch {
                val apiResult = getSessionUsecase.execute()
                var commandMap = mapOf<Int, List<String>>()
                if(apiResult.data != null) {
                    commandMap = createCommandMap(apiResult.data!!)
                }


                withContext(Dispatchers.Main) {
                    var bleDevice : BleDevice = BleDevice(deviceData.address, gatt,deviceData.name,apiResult.data, null)
                    bleDevice.commandMap = commandMap
                    connectedDeviceMap.put(deviceData.address, bleDevice)
                    val callback = connectingDeviceCallbackMap.get(deviceData.address)
                    callback?.onConnectionSuccess(BleDevice(deviceData.address, null, deviceData.name,null, null), true)
                    sendData()

                }

            }

    }
    fun getIntensityCommand(intensity:Int) :String {
        if(intensity == 0){
            return getString(R.string.change_intensity_1_command)
        } else if(intensity == 1){
            return  getString(R.string.change_intensity_2_command)
        }else{
            return getString(R.string.change_intensity_3_command)
        }
    }

    fun getPatternCommand(pattern: Int) : String {
        if(pattern == 0) {
            return getString(R.string.change_mode_2_command)
        } else{
            return getString(R.string.change_mode_1_command)
        }
    }

    fun createCommandMap(session: Session) : Map<Int, List<String>>{
        var sum = 0
        var commandMap = mutableMapOf<Int, List<String>>()
        for(therapy in session.therapyList){
            var timeSofar = sum
            var intesityCommand:String = getIntensityCommand(therapy.itensity)
            var patternCommand : String =  getPatternCommand(therapy.pattern)
            var commandArray = mutableListOf<String>()
            commandArray.add(intesityCommand)
            commandArray.add(patternCommand)
            commandMap.put(timeSofar, commandArray)
            if(therapy.time > 10) {
                var therapyChangetime = therapy.time / 10
                for (i in 0..(therapyChangetime - 1)){
                    var commandArray1 = mutableListOf<String>()
                    var time =  timeSofar + (10 * i) + 5
                    commandArray1.add(getPatternCommand(Math.abs(therapy.pattern -1)))
                    commandArray1.add(getPatternCommand(Math.abs(therapy.pattern)))
                    commandMap.put(time, commandArray1)
                }
            }
            sum = sum + therapy.time
        }
        var commandArray2 = mutableListOf<String>()
        var pauseCommand = getString(R.string.pause_command)
        commandArray2.add(pauseCommand)
        commandMap.put(sum, commandArray2)
        return commandMap
    }

    private fun decodeStatusData(char : BluetoothGattCharacteristic) : DeviceStatus?{
        val data =  char.value
        if (data != null && data.size > 0) {
            val udata = IntArray(data.size)
            for (i in data.indices) {
                udata[i] = (data[i] and 0xFF.toByte()).toInt()
            }
            val pressure_top = udata[0] * 256 + udata[1]
            val battery_val = udata[2]
            val pressure_mid = udata[3] * 256 + udata[4]
            val unidentified_1 = udata[5]
            val pressure_low = udata[6] * 256 + udata[7] // Bottom Zone\
            val pwm_top = udata[8]
            val pwm_mid = udata[9]
            val pwm_low = udata[10]
            val keep_work_time = udata[11] * 256 + udata[12]
            val ap_work_mode = udata[13]
            val intensity_flag = udata[14] - 1
            val mode_step = udata[15]
            val step_time = udata[16]
            val pause_flag = udata[17]
            val deviceStatus : DeviceStatus = DeviceStatus(pressure_top, battery_val, pressure_mid, unidentified_1, pressure_low, pwm_top, pwm_mid, pwm_low, keep_work_time, ap_work_mode, intensity_flag, mode_step, step_time, pause_flag)
            return deviceStatus
        } else{
            return null
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
    fun connectnew(){

    }

    fun connect(address: String ,deviceConnectionListener: DeviceConnectionListener): Boolean {
        connectingDeviceCallbackMap.put(address, deviceConnectionListener)
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                } else{
                }
                var bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback)
                var bleDevice: BleDevice = BleDevice(device.address,null,device.name, null, null)
                connectingDeviceMap.put(address, bleDevice)
                return false

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
        for (i in connectingDeviceMap.keys){
            if(connectingDeviceMap.get(i)?.gatt != null){
                close(connectingDeviceMap.get(i)?.gatt!!)
            }

        }

        return super.onUnbind(intent)
    }


    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() : BleServiceNew {
            return this@BleServiceNew
        }
    }

    fun writeCharacteristic(gatt: BluetoothGatt, data:String) {
        val service = gatt.getService(UUID.fromString(writeServiceUUIDString))
        val sendChar =  service.getCharacteristic(UUID.fromString(writeCharUUIDString))
        sendChar.setValue(data)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        gatt.writeCharacteristic(sendChar)
        if(data.equals(getString(R.string.start_command))){
            startDeviceUpdator(gatt.device.address)
        } else if (data.equals(getString(R.string.pause_command))){
            stopDeviceUpdator(gatt.device.address)
        }

    }

    @Synchronized
    fun startDeviceUpdator(deviceId : String){
        var updator:DeviceUpdator = DeviceUpdator(deviceId)
        val periodicUpdate: Runnable = object : Runnable {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            override fun run() {
                updator.handler.postDelayed(this, 3000)
                updatedevice(deviceId)
            }
        }
        updator.runnable = periodicUpdate
        if (deviceUpdatorMap.containsKey(deviceId)){
            var deviceUpdator  = deviceUpdatorMap.get(deviceId)
            deviceUpdator?.stop()
            deviceUpdatorMap.put(deviceId, updator)
        } else{
            deviceUpdatorMap.put(deviceId, updator)
        }
        updator.start()
    }

    fun stopDeviceUpdator(deviceId : String){
        if(deviceUpdatorMap.containsKey(deviceId)){
            var deviceUpdator =  deviceUpdatorMap.get(deviceId)
            deviceUpdator?.stop()
        }
    }

    @Synchronized
    fun updatedevice(deviceId:String){
        if (connectedDeviceMap.containsKey(deviceId)){
            var connectedDevice = connectedDeviceMap.get(deviceId)
            var session = connectedDevice?.session
            val elapseTime =  session?.elapseTime
            if(elapseTime != null){
                if (connectedDevice != null) {
                    if(connectedDevice.commandMap != null){
                        var commandMap = connectedDevice.commandMap
                        if (commandMap != null) {
                            if(commandMap.containsKey(elapseTime.plus(1))){
                                var commands : List<String>? = commandMap.get(elapseTime.plus(1))
                                if (commands != null) {
                                    for (command in commands) {
                                        connectedDevice.gatt?.let {
                                            writeCharacteristic(it, command)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                session?.elapseTime = elapseTime.plus(1)
                connectedDevice?.session = session
                connectedDevice?.deviceId?.let {
                    connectedDeviceMap.put(it, connectedDevice)
                    sendData()
                }
                if (connectedDevice != null) {
                    updateRemoteSessionData(elapseTime.plus(1), connectedDevice.deviceId)
                }
            }

        }
    }

    @Synchronized
    fun updateRemoteSessionData(elapseTime :Int, deviceId :String){
        ioScope.launch {
            println("update session data in remote server++++++")
            val apiResult = updateSessionUsecase.execute(elapseTime, deviceId)
        }
    }

}