package com.shehan.navapp.ui.session

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shehan.navapp.data.usecase.GetSessionUsecase
import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.models.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SesionViewModel(
    private val app: Application,
    private val getSessionUsecase: GetSessionUsecase
) : ViewModel() {
    lateinit var deviceId : String

//    val session: MutableLiveData<Resource<Session>> = MutableLiveData()
//    fun getSession() {
//        viewModelScope.launch(Dispatchers.IO) {
//            session.postValue(Resource.Loading())
//            try {
//                if(isNetworkAvailable(app)) {
//                    val apiResult = getSessionUsecase.execute()
//
//                    session.postValue(apiResult)
//                } else{
//                    session.postValue(Resource.Error("Internet is not available"))
//                }
//
//            } catch (e : Exception) {
//                session.postValue(Resource.Error(e.message.toString()))
//            }
//
//        }
//
//    }
//    private fun isNetworkAvailable(context: Context?):Boolean{
//        if (context == null) return false
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//            if (capabilities != null) {
//                when {
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
//                        return true
//                    }
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
//                        return true
//                    }
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
//                        return true
//                    }
//                }
//            }
//        } else {
//            val activeNetworkInfo = connectivityManager.activeNetworkInfo
//            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
//                return true
//            }
//        }
//        return false
//
//    }
}