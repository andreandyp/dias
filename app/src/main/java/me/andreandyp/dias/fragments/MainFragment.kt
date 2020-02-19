package me.andreandyp.dias.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.andreandyp.dias.R
import me.andreandyp.dias.adapters.AlarmasAdapter
import me.andreandyp.dias.databinding.MainFragmentBinding
import me.andreandyp.dias.viewmodels.MainViewModel
import me.andreandyp.dias.viewmodels.MainViewModelFactory

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val dias =
            listOf<String>(
                getString(R.string.lunes),
                getString(R.string.martes),
                getString(R.string.miercoles),
                getString(R.string.jueves),
                getString(R.string.viernes),
                getString(R.string.sabado),
                getString(R.string.domingo)
            )

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(activity!!.application, dias)
        )[MainViewModel::class.java]

        binding.alarmas.adapter = AlarmasAdapter(context, viewModel).apply {
            listaAlarmas = viewModel.alarmas
        }

        /*ArrayAdapter.createFromResource(
            context!!,
            R.array.dias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            binding.alarmas.hora.adapter = adapter
        }*/



        return binding.root
    }

}
