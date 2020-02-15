package me.andreandyp.dias.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import me.andreandyp.dias.R
import me.andreandyp.dias.databinding.AlarmaItemBinding


class AlarmasAdapter(var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var lista: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AlarmViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                AlarmViewHolder.LAYOUT,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AlarmViewHolder
        holder.alarmaItemBinding.apply {
            dia.text = lista[position]
            hora.text = "±0:00"
            detalles.setOnClickListener {
                if (containerLayout.visibility == View.GONE) {
                    containerLayout.visibility = View.VISIBLE
                    val flechaUp = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_up_black,
                        null
                    )
                    detalles.setImageDrawable(flechaUp)
                    alarmaConstraint.background = ColorDrawable(context!!.getColor(R.color.grey))
                } else {
                    containerLayout.visibility = View.GONE
                    val flechaDown = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_down_black,
                        null
                    )
                    detalles.setImageDrawable(flechaDown)
                    alarmaConstraint.background = ColorDrawable(context!!.getColor(android.R.color.white))
                }

            }
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
