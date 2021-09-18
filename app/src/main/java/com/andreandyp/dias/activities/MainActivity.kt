package com.andreandyp.dias.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.andreandyp.dias.R
import com.andreandyp.dias.bd.DiasDatabase
import com.andreandyp.dias.bd.SunriseRoomDataSource
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
import com.andreandyp.dias.viewmodels.MainViewModel
import com.andreandyp.dias.viewmodels.MainViewModelFactory
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        createViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        habilitarLocalizacion()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                habilitarLocalizacion()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            val ringtone = RingtoneManager.getRingtone(this, uri)
            viewModel.onRingtoneSelected(requestCode, uri, ringtone.getTitle(this))
        }
    }

    private fun habilitarLocalizacion() {
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
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

        val db = DiasDatabase.getDatabase(this)
        val preferencias: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            application,
            dias,
        )
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
