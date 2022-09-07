package com.shehan.navapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shehan.navapp.R
import com.shehan.navapp.models.BleDevice

class ScandeviceApdapter(private val mList: MutableList<BleDevice>,  val deviceSelectListener : DeviceSelectListener)  : RecyclerView.Adapter<ScandeviceApdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val deviceName: TextView = itemView.findViewById(R.id.devicename)
        val deviceId: TextView = itemView.findViewById(R.id.deviceId)
        val deviceHopst: LinearLayout = itemView.findViewById(R.id.deviceHost)
//        val itemView : View =  itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = mList[position]
        holder.deviceName.text = device.deviceName
        holder.deviceId.text = device.deviceId
        holder.deviceHopst.setOnClickListener(View.OnClickListener {
            deviceSelectListener.onDeviceSelect(device.deviceId)
        })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun addDevice(device : BleDevice){
        println("device is added")
        mList.add(device)
        println(mList.size)
        notifyDataSetChanged()
    }


    interface DeviceSelectListener {
        fun onDeviceSelect(deviceId : String)
    }
}