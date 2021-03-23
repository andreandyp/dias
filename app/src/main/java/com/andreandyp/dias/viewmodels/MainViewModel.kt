package com.andreandyp.dias.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.repository.DiasRepository
import com.andreandyp.dias.utils.AlarmUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField

/**
 * ViewModel para la lista de alarmas.
 * @constructor Se llama desde [MainViewModelFactory].
 * @property [app] La aplicación para obtener los recursos.
 * @property [dias] Una [List] con los días de la semana.
 */
class MainViewModel(
    private val repository: DiasRepository,
    val app: Application,
    val dias: List<String>,
) : AndroidViewModel(app) {
    val alarmas = mutableListOf<Alarma>()

    private val _siguienteAlarma = MutableLiveData<Alarma>()
    val siguienteAlarma: LiveData<Alarma> = _siguienteAlarma

    private val _origenDatos = MutableLiveData<Origen>()
    val origenDatos: LiveData<Origen> = _origenDatos

    private val _actualizandoDatos = MutableLiveData(false)
    val actualizandoDatos: LiveData<Boolean> = _actualizandoDatos

    init {
        val diaSiguienteAlarma = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]

        for (i: Int in dias.indices) {
            val alarma = configurarAlarma(i, diaSiguienteAlarma == i + 1)
            alarmas.add(alarma)
        }

        obtenerUbicacion(false)
    }

    fun obtenerUbicacion(forzarActualizacion: Boolean) {
        val fusedLocationClient: FusedLocationProviderClient? =
            LocationServices.getFusedLocationProviderClient(app.applicationContext)
        _actualizandoDatos.value = true

        val permisoCoarseLocation = ActivityCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permisoOtorgado = PackageManager.PERMISSION_GRANTED

        if (permisoCoarseLocation != permisoOtorgado) {
            Log.i("PRUEBA", "SIN PERMISO")
            viewModelScope.launch {
                obtenerSiguienteAlarma(null, forzarActualizacion)
            }
            return
        }

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            Log.i("PRUEBA", location.toString())
            viewModelScope.launch {
                obtenerSiguienteAlarma(location, forzarActualizacion)
            }
        }?.addOnFailureListener {
            viewModelScope.launch {
                obtenerSiguienteAlarma(null, forzarActualizacion)
            }
        }
    }

    fun onRingtoneSeleccionado(alarmaId: Int, uri: Uri?, ringtone: String) {
        alarmas[alarmaId].tono = ringtone
        alarmas[alarmaId].uriTono = uri.toString()
    }

    /**
     * Guarda el estado (on/off) de la alarma.
     */
    fun cambiarEstadoAlarma(alarma: Alarma) {
        establecerAlarma(alarma)
        repository.guardarEstadoAlarma(alarma)
    }

    private suspend fun obtenerSiguienteAlarma(ubicacion: Location?, forzarActualizacion: Boolean) {
        val amanecer = repository.obtenerAmanecerDiario(ubicacion, forzarActualizacion)
        Log.i("PRUEBA", amanecer.origen.toString())

        _origenDatos.value = amanecer.origen
        val siguienteDia = alarmas[amanecer.diaSemana - 1]
        siguienteDia.fechaHoraAmanecer = amanecer.fechaHoraLocal.toLocalDateTime()
        _siguienteAlarma.value = siguienteDia
        _actualizandoDatos.value = false
    }

    /**
     * Establecer la alarma con la hora de la alarma.
     * Si estamos manipulando la alarma de mañana, se encenderá o apagará la alarma siguiente,
     * si no, simplemente no se hará nada (el work manager se encargará de activarla).
     */
    private fun establecerAlarma(alarma: Alarma) {
        alarma.fechaHoraAmanecer?.let {
            val pendingIntent = AlarmUtils.crearIntentAlarma(app.applicationContext, alarma.id)

            if (alarma.encendida) {
                val horaAlarmaUTC = alarma.fechaHoraSonar!!.atZone(ZoneId.systemDefault())
                //horaAlarmaUTC = horaAlarmaUTC.withHour(14).withMinute(57).withDayOfMonth(17)
                Log.i("PRUEBA", horaAlarmaUTC.toString())
                AlarmUtils.encenderAlarma(
                    app.applicationContext,
                    horaAlarmaUTC.toInstant(),
                    pendingIntent
                )
            } else {
                AlarmUtils.apagarAlarma(app.applicationContext, pendingIntent)
            }
        }
    }

    private fun configurarAlarma(idAlarma: Int, esSiguienteAlarma: Boolean): Alarma {
        val alarma = Alarma(idAlarma, esSiguienteAlarma)

        alarma.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                sender as Alarma
                when (propertyId) {
                    BR.encendida -> cambiarEstadoAlarma(sender)
                    BR.vibrar -> repository.guardarVibrarAlarma(sender)
                    BR.horasDiferencia -> repository.guardarHorasAlarma(sender)
                    BR.minutosDiferencia -> repository.guardarMinAlarma(sender)
                    BR.momento -> repository.guardarMomentoAlarma(sender)
                    BR.tono -> repository.guardarTonoAlarma(sender)
                    BR.uriTono -> repository.guardarUriTonoAlarma(sender)
                    BR.fechaHoraAmanecer -> cambiarEstadoAlarma(sender)
                }
            }
        })

        alarma.dia = dias[alarma.id]

        return repository.establecerPreferenciasAlarma(alarma)
    }

}
