package com.andreandyp.dias.fragments

import android.Manifest
import android.content.Context
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
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import com.andreandyp.dias.repository.location.LocationRepository
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase
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
            listaAlarmas = viewModel.alarmas
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.obtenerUbicacion(true)
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
        viewModel.origenDatos.observe(viewLifecycleOwner) {
            binding.fuente.text = when (it) {
                Origen.INTERNET -> requireContext().getString(R.string.segun_internet)
                Origen.BD -> requireContext().getString(R.string.segun_bd)
                else -> requireContext().getString(R.string.segun_usuario)
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createViewModelFactory(): MainViewModelFactory {
        val dias = listOf(
            getString(R.string.lunes),
            getString(R.string.martes),
            getString(R.string.miercoles),
            getString(R.string.jueves),
            getString(R.string.viernes),
            getString(R.string.sabado),
            getString(R.string.domingo)
        )

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

        return MainViewModelFactory(
            getLastLocationUseCase,
            getTomorrowSunriseUseCase,
            saveAlarmSettingsUseCase,
            configureAlarmSettingsUseCase,
            isPermissionGranted(),
            requireActivity().application,
            dias,
        )
    }
}
