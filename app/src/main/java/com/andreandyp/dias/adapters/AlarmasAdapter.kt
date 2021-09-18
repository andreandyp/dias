package com.andreandyp.dias.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter.AlarmViewHolder
import com.andreandyp.dias.databinding.AlarmaItemBinding
import com.andreandyp.dias.databinding.HoraDialogBinding
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.viewmodels.MainViewModel
import com.google.android.material.button.MaterialButton

class AlarmasAdapter(private var context: Context?, private val viewModel: MainViewModel) :
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
            tvHoraAntesDespues.setOnClickListener {
                crearDialogHoras(alarm!!).show()
            }

            detalles.setOnClickListener {
                showHideAlarm(btnTono, detalles)
            }

            // Hacer click en la alarma es lo mismo que hacer click en la flecha
            alarmaConstraint.setOnClickListener {
                detalles.performClick()
            }

            btnTono.setOnClickListener {
                val tono = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                (context as AppCompatActivity).startActivityForResult(tono, alarm!!.id)
            }
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
        val antesDespues: NumberPicker = dialogView.antesDespues
        val masMenos = arrayOf("-", "+")
        antesDespues.displayedValues = masMenos
        antesDespues.maxValue = 1
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
            alarm.offsetHours = hora.value
            alarm.offsetMinutes = elementos[minutos.value].toInt()
            alarm.offsetType = antesDespues.value
            alarm.on = true
            this.dismiss()
        }
        dialogView.buttonCancelar.setOnClickListener {
            this.cancel()
        }
    }

    private fun showHideAlarm(btnTono: MaterialButton, detalles: ImageView) {
        if (btnTono.visibility == View.GONE) {
            btnTono.visibility = View.VISIBLE
            val flechaUp = ResourcesCompat.getDrawable(
                context!!.resources,
                R.drawable.ic_baseline_keyboard_arrow_up_24,
                null
            )
            detalles.setImageDrawable(flechaUp)
        } else {
            btnTono.visibility = View.GONE
            val flechaDown = ResourcesCompat.getDrawable(
                context!!.resources,
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                null
            )
            detalles.setImageDrawable(flechaDown)
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
