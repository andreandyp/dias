package com.andreandyp.dias.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter.AlarmaViewHolder
import com.andreandyp.dias.databinding.AlarmaItemBinding
import com.andreandyp.dias.databinding.HoraDialogBinding
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.viewmodels.MainViewModel

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
            horaAntesDespues.setOnClickListener {
                AlertDialog.Builder(context!!).create().apply {
                    val dialogView: HoraDialogBinding = DataBindingUtil.inflate(this.layoutInflater, R.layout.hora_dialog, null, false)
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
                    if (alarma!!.minutosDiferencia == 0) {
                        minutos.value = 0
                    } else {
                        minutos.value = elementos.indexOf(alarma!!.minutosDiferencia.toString())
                    }

                    // Poner la vista y el título
                    setView(dialogView.root)
                    setTitle("Establece la hora antes o después del amanecer")

                    // Poner listeners a los botones
                    dialogView.buttonAceptar.setOnClickListener {
                        // Actualizar los valores en alarma para guardarlos
                        alarma!!.horasDiferencia = hora.value
                        alarma!!.minutosDiferencia = elementos[minutos.value].toInt()
                        alarma!!.momento = antesDespues.value
                        alarma!!.encendida = true
                        this.dismiss()
                    }
                    dialogView.buttonCancelar.setOnClickListener {
                        this.cancel()
                    }

                }.show()
            }

            // Asignar listener para abrir y cerrar la alarma
            detalles.setOnClickListener {
                if (containerLayout.visibility == View.GONE) {
                    containerLayout.visibility = View.VISIBLE
                    val flechaUp = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_up_black,
                        null
                    )
                    detalles.setImageDrawable(flechaUp)
                } else {
                    containerLayout.visibility = View.GONE
                    val flechaDown = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_down_black,
                        null
                    )
                    detalles.setImageDrawable(flechaDown)
                }
            }

            // Hacer click en la alarma es lo mismo que hacer click en la flecha
            alarmaConstraint.setOnClickListener {
                detalles.performClick()
            }
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
