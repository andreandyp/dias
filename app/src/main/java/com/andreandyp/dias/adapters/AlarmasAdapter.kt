package com.andreandyp.dias.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter.AlarmViewHolder
import com.andreandyp.dias.databinding.AlarmaItemBinding
import com.andreandyp.dias.databinding.HoraDialogBinding
import com.andreandyp.dias.domain.Alarm

class AlarmasAdapter(private var context: Context?) :
    RecyclerView.Adapter<AlarmViewHolder>() {

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

            // Al dar click en el indicador de hora, se abre un dialog para ajustar la hora
            // Es mucho más eficiente que crearlo antes (comprobado)
            /*tvHoraAntesDespues.setOnClickListener {
                crearDialogHoras(alarm!!).show()
            }*/
        }
    }

    private fun crearDialogHoras(alarm: Alarm) = AlertDialog.Builder(context!!).create().apply {
        val dialogView: HoraDialogBinding = DataBindingUtil.inflate(
            this.layoutInflater,
            R.layout.hora_dialog,
            null,
            false
        )
        dialogView.alarm = alarm

        // Establecer valores a los pickers y configuraciones
        // val antesDespues: NumberPicker = dialogView.antesDespues
        val masMenos = arrayOf("-", "+")
        /*antesDespues.displayedValues = masMenos
        antesDespues.maxValue = 1*/
        val hora: NumberPicker = dialogView.hora
        hora.minValue = 0
        hora.maxValue = 3

        val minutos: NumberPicker = dialogView.minutos
        val elementos = arrayOf("00", "15", "30", "45")
        minutos.displayedValues = elementos
        minutos.minValue = 0
        minutos.maxValue = elementos.size - 1
        if (alarm.offsetMinutes == 0) {
            minutos.value = 0
        } else {
            minutos.value = elementos.indexOf(alarm.offsetMinutes.toString())
        }

        // Poner la vista y el título
        setView(dialogView.root)
        setTitle("Establece la hora antes o después del amanecer")

        // Poner listeners a los botones
        dialogView.buttonAceptar.setOnClickListener {
            // Actualizar los valores en alarma para guardarlos
            /*alarm.offsetHours = hora.value
            alarm.offsetMinutes = elementos[minutos.value].toInt()
            alarm.offsetType = antesDespues.value
            alarm.on = true*/
            this.dismiss()
        }
        dialogView.buttonCancelar.setOnClickListener {
            this.cancel()
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
