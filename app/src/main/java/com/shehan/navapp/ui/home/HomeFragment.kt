package com.shehan.navapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shehan.navapp.MainActivity
import com.shehan.navapp.R
import com.shehan.navapp.adapter.ScandeviceApdapter
import com.shehan.navapp.databinding.FragmentHomeBinding
import com.shehan.navapp.models.BleDevice

class HomeFragment : Fragment(), ScandeviceApdapter.DeviceSelectListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
     var deviceArray: MutableList<BleDevice> = mutableListOf<BleDevice>()
    private lateinit var  leDeviceListAdapter : ScandeviceApdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this,HomeViewModelFactory(activity)).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.recyclerview

        binding.connectbtn.setOnClickListener(View.OnClickListener {
            (activity as MainActivity).navigate(R.id.action_navigation_home_to_scandeviceFragmant)
//            (activity as MainActivity).navigate(R.id.action_navigation_home_to_sesionFragment)
        })
        (activity as MainActivity).deviceLiveData.observe(viewLifecycleOwner) {

            deviceArray.clear()
            for (data in it.values){
                deviceArray.add(data)
            }


            println(deviceArray.size)
            leDeviceListAdapter.notifyDataSetChanged()

        }
        leDeviceListAdapter = ScandeviceApdapter(deviceArray, this)
        recyclerView.adapter = leDeviceListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeviceSelect(deviceId: String) {
        println(deviceId)
        val bundle = bundleOf("deviceId" to deviceId)
        (activity as MainActivity).getNavigationController().navigate(R.id.action_navigation_home_to_deviceDetailsFragment,bundle)
    }
}