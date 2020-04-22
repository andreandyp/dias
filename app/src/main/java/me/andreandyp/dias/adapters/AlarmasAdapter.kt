package me.andreandyp.dias.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.andreandyp.dias.R
import me.andreandyp.dias.adapters.AlarmasAdapter.AlarmaViewHolder
import me.andreandyp.dias.databinding.AlarmaItemBinding
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.viewmodels.MainViewModel

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
        val modoNocturno =
            context!!.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        holder.alarmaItemBinding.apply {
            alarma = listaAlarmas[position]

            // Asignar listeners a las propiedades de la alarma para guardar los datos
            alarma!!.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    sender as Alarma
                    when (propertyId) {
                        BR.encendida -> viewModel.cambiarEstadoAlarma(sender)
                        BR.vibrar -> viewModel.cambiarVibrarAlarma(sender)
                        BR.horasDiferencia -> viewModel.cambiarHorasAlarma(sender)
                        BR.minutosDiferencia -> viewModel.cambiarMinAlarma(sender)
                        BR.momento -> viewModel.cambiarMomentoAlarma(sender)
                    }
                }
            })

            // Crear diálogo para mostrar los pickers de hora y minutos
            val dialog = AlertDialog.Builder(context!!).create().apply {
                val dialogView = View.inflate(context, R.layout.hora_dialog, null)

                // Establecer valores a los pickers y configuraciones
                val antesDespues: NumberPicker = dialogView.findViewById(R.id.antes_despues)
                val masMenos = arrayOf("-", "+")
                antesDespues.displayedValues = masMenos
                antesDespues.maxValue = 1
                antesDespues.value = masMenos.indexOf(alarma!!.momento.toString())
                val hora: NumberPicker = dialogView.findViewById(R.id.hora)
                hora.minValue = 0
                hora.maxValue = 3
                hora.value = alarma!!.horasDiferencia

                val minutos: NumberPicker = dialogView.findViewById(R.id.minutos)
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
                setView(dialogView)
                setTitle("Establece la hora antes o después del amanecer")

                // Poner listeners a los botones
                dialogView.findViewById<MaterialButton>(R.id.button_aceptar).setOnClickListener {
                    // Actualizar los valores en alarma para guardarlos
                    alarma!!.horasDiferencia = hora.value
                    alarma!!.minutosDiferencia = elementos[minutos.value].toInt()
                    alarma!!.momento = antesDespues.value
                    this.dismiss()
                }
                dialogView.findViewById<MaterialButton>(R.id.button_cancelar).setOnClickListener {
                    this.cancel()
                }
            }

            // Asignar listeners para hora y minutos de alarma
            horaAntesDespues.setOnClickListener {
                dialog.show()
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
                    alarmaConstraint.background =
                        if (modoNocturno == Configuration.UI_MODE_NIGHT_YES) {
                            ColorDrawable(context!!.getColor(R.color.greyInverse))
                        } else {
                            ColorDrawable(context!!.getColor(R.color.grey))
                        }
                } else {
                    containerLayout.visibility = View.GONE
                    val flechaDown = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_down_black,
                        null
                    )
                    detalles.setImageDrawable(flechaDown)
                    alarmaConstraint.background =
                        if (modoNocturno == Configuration.UI_MODE_NIGHT_YES) {
                            ColorDrawable(context!!.getColor(android.R.color.transparent))
                        } else {
                            ColorDrawable(context!!.getColor(android.R.color.white))
                        }
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
