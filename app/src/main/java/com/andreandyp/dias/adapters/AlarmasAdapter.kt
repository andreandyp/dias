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
import com.andreandyp.dias.adapters.AlarmasAdapter.AlarmaViewHolder
import com.andreandyp.dias.databinding.AlarmaItemBinding
import com.andreandyp.dias.databinding.HoraDialogBinding
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.viewmodels.MainViewModel
import com.google.android.material.button.MaterialButton

/**
 * Adapter para la lista de alarmas.
 * Hereda de [RecyclerView.Adapter] que necesita de un ViewHolder, en este caso, [AlarmaViewHolder].
 * @property [context] El contexto para obtener recursos como los íconos y los colores.
 * @property [viewModel] Para acceder a las funciones del viewModel.
 */
class AlarmasAdapter(private var context: Context?, private val viewModel: MainViewModel) :
    RecyclerView.Adapter<AlarmaViewHolder>() {

    /**
     * [List] de [Alarma].
     * Cada alarma contiene los datos guardados en las shared preferences.
     */
    var listaAlarmas: List<Alarma> = emptyList()

    /**
     * Crear toda la vista de las alarmas.
     * Inflamos el ViewHolder con [DataBindingUtil] en vez del [LayoutInflater] predeterminado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmaViewHolder =
        AlarmaViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                AlarmaViewHolder.LAYOUT,
                parent,
                false
            )
        )

    /**
     * Asignar el tamaño de la lista de alarmas para que el recycler view sepa cuántos elementos va a renderizar.
     */
    override fun getItemCount(): Int = listaAlarmas.size

    /**
     * Cada vez que hace scroll la pantalla, aquí se recicla la vista y se asignan valores y listeners.
     */
    override fun onBindViewHolder(holder: AlarmaViewHolder, position: Int) {
        holder.alarmaItemBinding.apply {
            alarma = listaAlarmas[position]

            // Al dar click en el indicador de hora, se abre un dialog para ajustar la hora
            // Es mucho más eficiente que crearlo antes (comprobado)
            tvHoraAntesDespues.setOnClickListener {
                crearDialogHoras(alarma!!).show()
            }

            detalles.setOnClickListener {
                mostrarOcultarAlarma(btnTono, detalles)
            }

            // Hacer click en la alarma es lo mismo que hacer click en la flecha
            alarmaConstraint.setOnClickListener {
                detalles.performClick()
            }

            btnTono.setOnClickListener {
                val tono = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                (context as AppCompatActivity).startActivityForResult(tono, alarma!!._id)
            }
        }
    }

    private fun crearDialogHoras(alarma: Alarma) = AlertDialog.Builder(context!!).create().apply {
        val dialogView: HoraDialogBinding = DataBindingUtil.inflate(
            this.layoutInflater,
            R.layout.hora_dialog,
            null,
            false
        )
        dialogView.alarma = alarma

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
        if (alarma.minutosDiferencia == 0) {
            minutos.value = 0
        } else {
            minutos.value = elementos.indexOf(alarma.minutosDiferencia.toString())
        }

        // Poner la vista y el título
        setView(dialogView.root)
        setTitle("Establece la hora antes o después del amanecer")

        // Poner listeners a los botones
        dialogView.buttonAceptar.setOnClickListener {
            // Actualizar los valores en alarma para guardarlos
            alarma.horasDiferencia = hora.value
            alarma.minutosDiferencia = elementos[minutos.value].toInt()
            alarma.momento = antesDespues.value
            alarma.encendida = true
            this.dismiss()
        }
        dialogView.buttonCancelar.setOnClickListener {
            this.cancel()
        }
    }

    private fun mostrarOcultarAlarma(btnTono: MaterialButton, detalles: ImageView) {
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

    /**
     * ViewHolder para la alarma.
     * @property [alarmaItemBinding] el binding que inflamos en [AlarmasAdapter.onCreateViewHolder].
     * Hereda de [RecyclerView.ViewHolder] y lo inicializamos esta clase con la vista del binding.
     * Con [LayoutRes] obtenemos más fácilmente el XML que representa a cada alarma.
     */
    class AlarmaViewHolder(var alarmaItemBinding: AlarmaItemBinding) :
        RecyclerView.ViewHolder(alarmaItemBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.alarma_item
        }
    }

}
