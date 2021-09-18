package com.andreandyp.dias.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.andreandyp.dias.bd.DiasDatabase
import com.andreandyp.dias.bd.SunriseRoomDataSource
import com.andreandyp.dias.databinding.MainFragmentBinding
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.location.GMSLocationDataSource
import com.andreandyp.dias.network.SunriseRetrofitDataSource
import com.andreandyp.dias.network.SunriseSunsetAPI
import com.andreandyp.dias.preferences.AlarmSharedPreferencesDataSource
import com.andreandyp.dias.preferences.SunriseSharedPreferencesDataSource
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.receivers.PosponerReceiver
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import com.andreandyp.dias.repository.location.LocationRepository
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase
import com.andreandyp.dias.utils.Constants
import com.andreandyp.dias.utils.NotificationUtils
import com.andreandyp.dias.viewmodels.MainViewModel
import com.andreandyp.dias.viewmodels.MainViewModelFactory
import com.google.android.gms.location.LocationServices

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by activityViewModels {
        createViewModelFactory()
    }


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
            viewModel.fetchLocation(true)
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
                Origen.INTERNET -> requireContext().getString(R.string.segun_internet)
                Origen.BD -> requireContext().getString(R.string.segun_bd)
                else -> requireContext().getString(R.string.segun_usuario)
            }
        }
        viewModel.alarmStatusUpdated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { alarm ->
                val alarmPendingIntent = createAlarmPendingIntent(alarm.id)
                if (alarm.on) {
                    viewModel.onAlarmOn(alarm.ringingAt!!.toInstant(), alarmPendingIntent)
                } else {
                    val snoozePendingIntent = createSnoozePendingIntent()
                    viewModel.onAlarmOff(alarmPendingIntent, snoozePendingIntent)
                }
            }
        }
    }

    private fun createAlarmPendingIntent(alarmId: Int): PendingIntent {
        val context = requireContext()
        val alarmIntent = Intent(context, AlarmaReceiver::class.java)
        alarmIntent.putExtra(context.getString(R.string.notif_id_intent), alarmId)
        return PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun createSnoozePendingIntent(): PendingIntent {
        val snoozeIntent = Intent(context, PosponerReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            Constants.SNOOZE_ALARM_CODE,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createViewModelFactory(): MainViewModelFactory {
        val db = DiasDatabase.getDatabase(requireContext())
        val preferencias: SharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val gmsLocationDataSource = GMSLocationDataSource(fusedLocationClient)

        /* Mientras se hace el cambio a Clean */
        val locationRepository = LocationRepository(gmsLocationDataSource)
        val getLastLocationUseCase = GetLastLocationUseCase(locationRepository)
        val sunriseSharedPreferencesDataSource = SunriseSharedPreferencesDataSource(preferencias)
        val sunriseRoomDataSource = SunriseRoomDataSource(db)
        val sunriseRetrofitDataSource = SunriseRetrofitDataSource(
            SunriseSunsetAPI.sunriseSunsetService
        )
        val sunriseRepository = SunriseRepository(
            sunriseSharedPreferencesDataSource,
            sunriseRoomDataSource,
            sunriseRetrofitDataSource
        )
        val getTomorrowSunriseUseCase = GetTomorrowSunriseUseCase(sunriseRepository)

        val alarmSharedPreferencesDataSource = AlarmSharedPreferencesDataSource(preferencias)
        val alarmsRepository = AlarmsRepository(alarmSharedPreferencesDataSource)
        val saveAlarmSettingsUseCase = SaveAlarmSettingsUseCase(alarmsRepository)
        val configureAlarmSettingsUseCase = ConfigureAlarmSettingsUseCase(alarmsRepository)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        return MainViewModelFactory(
            getLastLocationUseCase,
            getTomorrowSunriseUseCase,
            saveAlarmSettingsUseCase,
            configureAlarmSettingsUseCase,
            isPermissionGranted(),
            alarmManager
        )
    }
}
