package com.andreandyp.dias.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter.AlarmViewHolder
import com.andreandyp.dias.databinding.AlarmaItemBinding
import com.andreandyp.dias.domain.Alarm

class AlarmasAdapter : RecyclerView.Adapter<AlarmViewHolder>() {

    var alarms: List<Alarm> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder =
        AlarmViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                AlarmViewHolder.LAYOUT,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = alarms.size

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.alarmaItemBinding.apply {
            alarm = alarms[position]
        }
    }

    class AlarmViewHolder(var alarmaItemBinding: AlarmaItemBinding) :
        RecyclerView.ViewHolder(alarmaItemBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.alarma_item
        }
    }

}
