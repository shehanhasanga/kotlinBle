package com.shehan.navapp.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shehan.navapp.MainActivity
import com.shehan.navapp.R
import com.shehan.navapp.adapter.ScandeviceApdapter
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.listener.DeviceConnectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScandeviceFragmant : Fragment() , ScandeviceApdapter.DeviceSelectListener,DeviceConnectionListener  {
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var adapter :  BluetoothAdapter
    private lateinit var bluetoothLeScanner :  BluetoothLeScanner
    private var scanning : Boolean = false
    private val handler : Handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD : Long =  10000
    private lateinit var scandeviceMap : MutableMap<String, BluetoothDevice>
    private lateinit var viewModel: ScandeviceFragmantViewModel
    private lateinit var recyclerView: RecyclerView
    lateinit var deviceArray: MutableList<BleDevice>
    private lateinit var  leDeviceListAdapter :ScandeviceApdapter


    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
//            println(result.device.address)
            if (!scandeviceMap.containsKey(result.device.address)) {
                println("new device found")
                scandeviceMap.put(result.device.address, result.device)
                var bledevicename = if (activity?.let {
                        it?.applicationContext?.let { it1 ->
                            ActivityCompat.checkSelfPermission(
                                it1,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        }
                    } != PackageManager.PERMISSION_GRANTED
                ) {
                    "unknown"
                } else {
                    result.device.name
                }
                var deviceName = "unknown"
                if (result.device.name != null) {
                    deviceName =result.device.name
                }
                var device = BleDevice(result.device.address, null,  deviceName   , null, null)
//                leDeviceListAdapter.addDevice(device)
                deviceArray.add(device)
                leDeviceListAdapter.notifyDataSetChanged()
            }

        }
    }

//    bluetoothManager.getAdapter()
//    private val blueToothScanner :
//    companion object {
//        fun newInstance() = ScandeviceFragmant()
//    }

    init {

    }

    private fun scanLeDevice() {
        leDeviceListAdapter.notifyDataSetChanged()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            Toast.makeText(context,"Permission denied", Toast.LENGTH_LONG).show()
//            return
        }
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                println("stopped scanning")
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private fun stopScan() {
        scanLeDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapter = bluetoothManager.adapter
        bluetoothLeScanner = adapter.bluetoothLeScanner
        var view  = inflater.inflate(R.layout.fragment_scandevice_fragmant, container, false)
        recyclerView =  view.findViewById<RecyclerView>(R.id.recyclerview)


        view.findViewById<ImageView>(R.id.scandevice).setOnClickListener(View.OnClickListener {
//            (activity as MainActivity).connecttoDevice();
            scanLeDevice()
        })



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScandeviceFragmantViewModel::class.java)
        scandeviceMap = viewModel.scandeviceMap
        deviceArray = viewModel.deviceArray
        leDeviceListAdapter = ScandeviceApdapter(deviceArray, this)
        recyclerView.adapter = leDeviceListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

//        CoroutineScope(Dispatchers.Default).launch {
//            (activity as MainActivity).getService()?._uiState?.collect { uiState ->
//                println("new event is camme ")
//            }
//        }

    }

    override fun onDeviceSelect(deviceId: String) {
        println(deviceId)
        stopScan()
        (activity as MainActivity).connecttoDevice(deviceId, this)
    }

    override fun onConnectionSuccess(device: BleDevice, success: Boolean) {
        activity?.let {
            lifecycleScope.launch(Dispatchers.Main){
                findNavController(it,R.id.nav_host_fragment_activity_main).popBackStack()
            }


        }

    }

    override fun onConnectionFail(device: BleDevice, success: Boolean) {
        TODO("Not yet implemented")
    }

}