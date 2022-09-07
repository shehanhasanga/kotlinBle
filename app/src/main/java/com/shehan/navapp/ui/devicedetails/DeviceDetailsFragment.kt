package com.shehan.navapp.ui.devicedetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.shehan.navapp.MainActivity
import com.shehan.navapp.R
import com.shehan.navapp.databinding.FragmentDeviceDetailsBinding
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.DeviceStatus
import com.shehan.navapp.utils.Algo

class DeviceDetailsFragment : Fragment() {
    private var _binding: FragmentDeviceDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var deviceId : String
    private lateinit var currentDevice: BleDevice



    private lateinit var viewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false)
        binding.graduated.setOnClickListener(View.OnClickListener {
            sendCommand(getString(R.string.change_mode_2_command))
        })
        binding.pulsalated.setOnClickListener(View.OnClickListener {
            sendCommand(getString(R.string.change_mode_1_command))
        })

        binding.poweroff.setOnClickListener(View.OnClickListener {
            sendCommand(getString(R.string.power_off_command))
        })
        binding.connectbtn.setOnClickListener(View.OnClickListener {
//            (activity as MainActivity).navigate(R.id.action_deviceDetailsFragment_to_sesionFragment)
            val bundle = bundleOf("deviceId" to deviceId)
            (activity as MainActivity).getNavigationController().navigate(R.id.action_deviceDetailsFragment_to_sesionFragment,bundle)
        })


        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(!p2){
                    return
                }
                if(deviceId != null) {
                    intensitySeekBarOnChange(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        binding.seekBar.setOnClickListener(View.OnClickListener {  })


        (activity as MainActivity).deviceLiveData.observe(viewLifecycleOwner) {
            if (it.containsKey(deviceId)){
                var device = it.get(deviceId)
                if (device != null) {
                    currentDevice =  device
                    binding.elapsetime.text = currentDevice.session?.elapseTime.toString()
                    device.status?.let { it1 -> processSensorData(it1) }
                }
            }

        }

        val root: View = binding.root

        return root
    }

    fun processSensorData(deviceStatus : DeviceStatus) {

        var pressureLow : Double = deviceStatus.pressure_low.toDouble()
        var pressureMid : Double = Math.min(
            deviceStatus.pressure_mid.toDouble(),
            pressureLow - Algo.randomDouble(0.0, 1.5)
        )
        var pressureTop : Double = Math.min(
            deviceStatus.pressure_top.toDouble(),
            pressureMid - Algo.randomDouble(0.0, 1.5)
        )

        binding.topval.text = Algo.formatPressure(pressureTop)
        binding.midval.text = Algo.formatPressure(pressureMid)
        binding.lowval.text = Algo.formatPressure(pressureLow)
        binding.topcolor.alpha = Algo.getAlpha(pressureTop)
        binding.midcoloer.alpha = Algo.getAlpha(pressureMid)
        binding.lowcolor.alpha = Algo.getAlpha(pressureLow)
        binding.seekBar.progress = deviceStatus.intensity_flag
        var pauseFlag = deviceStatus.pause_flag
        if(pauseFlag == 0){ // device is running
            binding.startTxt.text = "Pause"
            binding.startimage.setImageResource(R.drawable.pause)
        } else {
            binding.startTxt.text = "Start"
            binding.startimage.setImageResource(R.drawable.play)
        }

        binding.start.setOnClickListener(View.OnClickListener {
            if(pauseFlag == 0){
                //pause device
                sendCommand(getString(R.string.pause_command))
            } else if (pauseFlag == 1) {
                //start the device
                sendCommand(getString(R.string.start_command))
            }
        })

        var currentMode =  deviceStatus.ap_work_mode
        if(currentMode == 0) {
            binding.graduated.backgroundTintList = resources.getColorStateList(R.color.app_gray_background)
            binding.pulsalated.backgroundTintList = resources.getColorStateList(R.color.app_dark_gray)
        } else if(currentMode == 1) {
            binding.pulsalated.backgroundTintList = resources.getColorStateList(R.color.app_gray_background)
            binding.graduated.backgroundTintList = resources.getColorStateList(R.color.app_dark_gray)
        }



    }
    fun intensitySeekBarOnChange(i : Int){
        when(i){
            0 -> sendCommand(getString(R.string.change_intensity_1_command))
            1 -> sendCommand(getString(R.string.change_intensity_2_command))
            2 -> sendCommand(getString(R.string.change_intensity_3_command))
        }
    }

    fun sendCommand(command:String){
        if(currentDevice.gatt != null){
            (activity as MainActivity).sendCommand(currentDevice.gatt!!,command )
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        viewModel.deviceId = arguments?.getString("deviceId").toString()
        deviceId = viewModel.deviceId
        binding.deviceIdtxt.text = deviceId
    }
}