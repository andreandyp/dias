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
import me.andreandyp.dias.adapters.AlarmasAdapter.AlarmaViewHolder
import me.andreandyp.dias.databinding.AlarmaItemBinding
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.viewmodels.MainViewModel

/**
 * Adapter para la lista de alarmas
 * Hereda de [RecyclerView.Adapter] que necesita de un ViewHolder, en este caso [AlarmaViewHolder]
 * @param [context] el contexto para obtener recursos como los íconos y los colores
 * @param [viewModel] para acceder a las funciones del viewModel
 */
class AlarmasAdapter(private var context: Context?, private val viewModel: MainViewModel) :
    RecyclerView.Adapter<AlarmaViewHolder>() {

    /**
     * Lista de [Alarma]
     * Cada alarma contiene los datos guardados en las shared preferences.
     */
    var listaAlarmas: List<Alarma> = emptyList()

    /**
     * Crear toda la vista de las alarmas
     * Inflamos el ViewHolder con [DataBindingUtil] en vez del [LayoutInflater] predeterminado
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
     * Asignar el tamaño de la lista de alarmas para que el recycler view sepa cuántos elementos va a renderizar
     */
    override fun getItemCount(): Int = listaAlarmas.size

    /**
     * Cada vez que hace scroll la pantalla, aquí se recicla la vista y se asignan valores y listeners
     */
    override fun onBindViewHolder(holder: AlarmaViewHolder, position: Int) {
        holder.alarmaItemBinding.apply {
            val alarma = listaAlarmas[position]

            // Asignar textos y valores
            dia.text = alarma.dia
            encender.isChecked = alarma.encendida
            vibrar.isChecked = alarma.vibrar

            // Asignar listeners a encender y vibrar
            encender.setOnCheckedChangeListener { _, isChecked ->
                // Obtenemos el viewModel que recibe el adapter al inicializarlo
                this@AlarmasAdapter.viewModel.cambiarEstadoAlarma(alarma._id, isChecked)
            }

            vibrar.setOnCheckedChangeListener { _, isChecked ->
                this@AlarmasAdapter.viewModel.cambiarVibrarAlarma(alarma._id, isChecked)
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
                    alarmaConstraint.background = ColorDrawable(context!!.getColor(R.color.grey))
                } else {
                    containerLayout.visibility = View.GONE
                    val flechaDown = context!!.resources.getDrawable(
                        R.drawable.keyboard_arrow_down_black,
                        null
                    )
                    detalles.setImageDrawable(flechaDown)
                    alarmaConstraint.background =
                        ColorDrawable(context!!.getColor(android.R.color.white))
                }

            }

            // Hacer click en la alarma es lo mismo que hacer click en la flecha
            alarmaConstraint.setOnClickListener {
                detalles.performClick()
            }
        }
    }

    /**
     * ViewHolder para la alarma
     * @param [alarmaItemBinding] el binding que inflamos en [AlarmasAdapter.onCreateViewHolder]
     * Hereda de [RecyclerView.ViewHolder] y lo inicializamos esta clase con la vista del binding
     * Con [LayoutRes] obtenemos más fácilmente el XML que representa a cada alarma
     */
    class AlarmaViewHolder(var alarmaItemBinding: AlarmaItemBinding) :
        RecyclerView.ViewHolder(alarmaItemBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.alarma_item
        }
    }

}
