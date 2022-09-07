package com.shehan.navapp.ui.session

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shehan.navapp.MainActivity
import com.shehan.navapp.R
import com.shehan.navapp.adapter.ScandeviceApdapter
import com.shehan.navapp.adapter.SessionItemApdapter
import com.shehan.navapp.data.util.Resource
import com.shehan.navapp.databinding.FragmentHomeBinding
import com.shehan.navapp.databinding.FragmentSesionBinding
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.Session
import com.shehan.navapp.models.TherapyConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class SesionFragment : Fragment() {
    private var _binding: FragmentSesionBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: SesionViewModel
    lateinit var deviceId : String
    var isLoading: Boolean = false
    private lateinit var recyclerView: RecyclerView
     var dataArray: MutableList<TherapyConfig>  = mutableListOf<TherapyConfig>()
    private lateinit var  therapyListAdapter : SessionItemApdapter
    private lateinit var currentDevice : BleDevice

    companion object {
        fun newInstance() = SesionFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSesionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SesionViewModel::class.java)
        viewModel.deviceId = arguments?.getString("deviceId").toString()
        deviceId = viewModel.deviceId

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel
        recyclerView = binding.recyclerview
        therapyListAdapter = SessionItemApdapter(dataArray)
        recyclerView.adapter =therapyListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        (activity as MainActivity).deviceLiveData.observe(viewLifecycleOwner) {
            if (it.containsKey(deviceId)){
                var device = it.get(deviceId)
                if (device != null) {
                    currentDevice =  device
                }
            }

        }

//        getsession()
    }

//    private fun getsession() {
//        viewModel.getSession()
//        viewModel.session.observe(viewLifecycleOwner, {response->
//            when(response){
//                is Resource.Success -> {
//                    hideProgressBar()
//                    response.data?.let {
//                        dataArray.clear()
//                        for (item in it.therapyList){
//                            println(item.progress)
//                            dataArray.add(item)
//                        }
//                        therapyListAdapter.notifyDataSetChanged()
//
//                    }
//                }
//                is Resource.Error -> {
//                    println(response.message)
//                    hideProgressBar()
//
//                }
//                is Resource.Loading -> {
//                    showProgressBar()
//                }
//            }
//        })
//    }

    private fun showProgressBar(){
        isLoading = true
//        fragmentNewsBinding.progressBar.visibility = View.VISIBLE
        println("show loading +++++++")
    }

    private fun hideProgressBar(){
        isLoading = false
        println("hide loading +++++++")
//        fragmentNewsBinding.progressBar.visibility = View.INVISIBLE
    }

}