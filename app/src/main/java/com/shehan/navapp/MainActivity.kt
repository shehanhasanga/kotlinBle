package com.shehan.navapp

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.shehan.navapp.databinding.ActivityMainBinding
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.listener.DeviceConnectionListener
import com.shehan.navapp.service.BleService
import com.shehan.navapp.service.BleServiceNew
import com.shehan.navapp.ui.session.SesionViewModel
import com.shehan.navapp.ui.session.SessionViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : AppCompatActivity(), Parcelable {
    @Inject
    lateinit var factory: SessionViewModelFactory
    lateinit var viewModel: SesionViewModel

    private lateinit var binding: ActivityMainBinding
    lateinit var bluetoothService : BleServiceNew
    private lateinit var snackbar : Snackbar
    lateinit var  toolbar : androidx.appcompat.widget.Toolbar
    lateinit var navController: NavController

    val deviceLiveData  = MutableLiveData<Map<String, BleDevice>>()


    fun sendCommand(gatt: BluetoothGatt ,data:String) {
        bluetoothService.writeCharacteristic(gatt, data)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as BleServiceNew.LocalBinder).getService()
            lifecycleScope.launch{
                bluetoothService!!.deviceData().collect { datamap ->
                    deviceLiveData.value = datamap


                }
            }



//            lifecycleScope(Dispatchers.Default).launch {
//                repeatOnLifecycle(Lifecycle.State.CREATED) {
//                    bluetoothService!!.messages().collect { uiState ->
//                        println("new event is camme +++++++++++")
//                    }
//                }
//            }

            bluetoothService?.let { bluetooth ->
                // call functions on service to check connection and connect to devices
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    println("Unable to initialize Bluetooth")
//                    finish()
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService
        }
    }

    fun getService() : BleServiceNew? {
        return bluetoothService;
    }

    fun connecttoDevice(deviceId : String,deviceConnectionListener: DeviceConnectionListener){
        if(bluetoothService != null){
            println("connecting to the device ++++")
        } else{
        }
        bluetoothService?.connect(deviceId, deviceConnectionListener)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
//        toolbar = binding.toolbar

        navController = findNavController(R.id.nav_host_fragment_activity_main)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val gattServiceIntent = Intent(this, BleServiceNew::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        requestPermission(binding.root)
        viewModel = ViewModelProvider(this,factory)
            .get(SesionViewModel::class.java)
    }

    fun getNavigationController(): NavController {
        return navController
    }

     fun navigate(id: Int){
        navController.navigate(id)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    constructor(parcel: Parcel) : this() {

    }

    fun showSnackBatMessage(message: String, view: View) {
        snackbar = Snackbar.make(view, message,
            Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun requestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED -> {
            }  else -> {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), 200)
            }

//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.BLUETOOTH_CONNECT
//                )
//            }
//
//            else -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.BLUETOOTH_CONNECT
//                )
//            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    fun writeCharacteristic(gatt: BluetoothGatt, data : String){
        bluetoothService.writeCharacteristic(gatt, data)
    }
}