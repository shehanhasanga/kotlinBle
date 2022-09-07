package com.shehan.navapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shehan.navapp.R
import com.shehan.navapp.models.BleDevice
import com.shehan.navapp.models.TherapyConfig

class SessionItemApdapter(private val mList: MutableList<TherapyConfig>)  : RecyclerView.Adapter<SessionItemApdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val pattern: TextView = itemView.findViewById(R.id.protocoltxt)
        val intensity: TextView = itemView.findViewById(R.id.intensity)
        val time: TextView = itemView.findViewById(R.id.time)
        val progress: ProgressBar = itemView.findViewById(R.id.progress)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val therapyConfig = mList[position]
        holder.pattern.text = "pattern" + therapyConfig.pattern.toString()
        holder.intensity.text = therapyConfig.itensity.toString()
        holder.time.text = therapyConfig.time.toString()
        if(therapyConfig.progress != null){
            var progressPercentage = ((therapyConfig.progress!!.toDouble() / therapyConfig.time.toDouble()) * 100).toInt()
            holder.progress.progress = progressPercentage
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


}