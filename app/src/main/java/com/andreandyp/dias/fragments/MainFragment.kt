package com.andreandyp.dias.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter
import com.andreandyp.dias.databinding.MainFragmentBinding
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.utils.AlarmUtils
import com.andreandyp.dias.utils.NotificationUtils
import com.andreandyp.dias.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        NotificationUtils.crearCanalNotificaciones(requireContext())

        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.alarmas.adapter = AlarmasAdapter(context, viewModel).apply {
            alarms = viewModel.alarms
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.setupNextAlarm(isPermissionGranted(), true)
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.vm = viewModel

        setUpObservers()

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

    private fun setUpObservers() {
        viewModel.dataOrigin.observe(viewLifecycleOwner) {
            binding.fuente.text = when (it) {
                Origin.INTERNET -> requireContext().getString(R.string.segun_internet)
                Origin.DATABASE -> requireContext().getString(R.string.segun_bd)
                else -> requireContext().getString(R.string.segun_usuario)
            }
        }
        viewModel.alarmStatusUpdated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { alarm ->
                val alarmPendingIntent = AlarmUtils.createAlarmPendingIntent(
                    requireContext(), alarm.id
                )
                if (alarm.on) {
                    viewModel.onAlarmOn(alarm.ringingAt!!.toInstant(), alarmPendingIntent)
                } else {
                    val snoozePendingIntent = AlarmUtils.createSnoozePendingIntent(requireContext())
                    viewModel.onAlarmOff(alarmPendingIntent, snoozePendingIntent)
                }
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
