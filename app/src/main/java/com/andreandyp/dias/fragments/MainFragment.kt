package com.andreandyp.dias.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter
import com.andreandyp.dias.databinding.MainFragmentBinding
import com.andreandyp.dias.utils.NotificationUtils
import com.andreandyp.dias.viewmodels.MainViewModel
import com.andreandyp.dias.viewmodels.MainViewModelFactory

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        NotificationUtils.crearCanalNotificaciones(requireContext())

        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val dias =
            listOf(
                getString(R.string.lunes),
                getString(R.string.martes),
                getString(R.string.miercoles),
                getString(R.string.jueves),
                getString(R.string.viernes),
                getString(R.string.sabado),
                getString(R.string.domingo)
            )

        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(requireActivity().application, dias)
        )[MainViewModel::class.java]

        binding.alarmas.adapter = AlarmasAdapter(context, viewModel).apply {
            listaAlarmas = viewModel.alarmas
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.obtenerUbicacion(true)
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.vm = viewModel

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ajustes_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ajustes -> {
                this.findNavController()
                    .navigate(MainFragmentDirections.mostrarAjustes())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
