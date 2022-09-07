package com.shehan.navapp.ui.session

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shehan.navapp.data.usecase.GetSessionUsecase

class SessionViewModelFactory(
    private val app: Application,
    private val getSessionUsecase: GetSessionUsecase
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SesionViewModel(
            app,
            getSessionUsecase
        ) as T
    }

}


