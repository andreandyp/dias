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
            listOf<String>("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        binding.alarmas.adapter = AlarmasAdapter(context).apply {
            lista = dias
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(activity!!.application)
        )[MainViewModel::class.java]
    }

}
