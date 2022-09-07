package com.shehan.navapp.ui.home

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shehan.navapp.MainActivity
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.service.BleServiceNew

class HomeViewModel(
    private val application: Activity) : ViewModel() {
    var deviceLiveData : MutableLiveData<Map<String, BleDevice>>
    init {
        deviceLiveData = (application as MainActivity).deviceLiveData
    }

}